package com.datnt.moviebooker.service;

import com.datnt.moviebooker.dto.ScreenRequest;
import com.datnt.moviebooker.dto.ScreenResponse;
import com.datnt.moviebooker.entity.Screen;
import com.datnt.moviebooker.entity.Theater;
import com.datnt.moviebooker.mapper.ScreenMapper;
import com.datnt.moviebooker.repository.ScreenRepository;
import com.datnt.moviebooker.repository.TheaterRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class ScreenService {
    private static final Logger logger = LoggerFactory.getLogger(ScreenService.class);
    private static final String SCREEN_CACHE_KEY = "screens";

    private final ScreenRepository screenRepository;
    private final TheaterRepository theaterRepository;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final ScreenMapper screenMapper;

    public Page<ScreenResponse> getAllScreens(Pageable pageable) {
        HashOperations<String, String, String> hashOps = redisTemplate.opsForHash();

        List<Screen> screens = hashOps.entries(SCREEN_CACHE_KEY).values().stream()
                .map(json -> parseJson(json, Screen.class))
                .filter(Objects::nonNull)
                .toList();

        if (screens.isEmpty()) {
            screens = screenRepository.findAll();
            screens.forEach(this::updateCache);
            redisTemplate.expire(SCREEN_CACHE_KEY, 10, TimeUnit.MINUTES);
        }

        List<ScreenResponse> screenResponses = screens.stream()
                .map(screenMapper::toResponse)
                .toList();

        return paginateList(screenResponses, pageable);
    }

    @Transactional
    public ScreenResponse createScreen(ScreenRequest request) {
        Theater theater = theaterRepository.findById(request.getTheaterId())
                .orElseThrow(() -> new RuntimeException("Theater not found!"));

        Screen screen = screenMapper.toEntity(request);
        screen.setTheater(theater);

        Screen savedScreen = screenRepository.save(screen);
        updateCache(savedScreen);

        return screenMapper.toResponse(savedScreen);
    }

    @Transactional
    public ScreenResponse updateScreen(Long id, ScreenRequest request) {
        return screenRepository.findById(id).map(screen -> {
            Theater theater = theaterRepository.findById(request.getTheaterId())
                    .orElseThrow(() -> new RuntimeException("Theater not found!"));

            screen.setTheater(theater);
            screen.setName(request.getName());

            Screen savedScreen = screenRepository.save(screen);
            updateCache(savedScreen);
            return screenMapper.toResponse(savedScreen);
        }).orElse(null);
    }

    @Transactional
    public void deleteScreen(Long id) {
        screenRepository.deleteById(id);
        redisTemplate.opsForHash().delete(SCREEN_CACHE_KEY, id.toString());
    }

    public Screen getScreenEntityById(Long id) {
        return screenRepository.findById(id).orElse(null);
    }

    public ScreenResponse getScreenById(Long id) {
        HashOperations<String, String, String> hashOps = redisTemplate.opsForHash();
        String cachedScreen = hashOps.get(SCREEN_CACHE_KEY, id.toString());

        if (cachedScreen != null) {
            Screen screen = parseJson(cachedScreen, Screen.class);
            assert screen != null;
            return screenMapper.toResponse(screen);
        }

        return screenRepository.findById(id).map(screen -> {
            updateCache(screen);
            return screenMapper.toResponse(screen);
        }).orElse(null);
    }

    private void updateCache(Screen screen) {
        try {
            redisTemplate.opsForHash().put(SCREEN_CACHE_KEY, screen.getId().toString(), objectMapper.writeValueAsString(screen));
        } catch (Exception e) {
            logger.error("Error saving screen to cache", e);
        }
    }

    private <T> T parseJson(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            logger.error("Error parsing JSON from cache", e);
            return null;
        }
    }

    private Page<ScreenResponse> paginateList(List<ScreenResponse> screens, Pageable pageable) {
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), screens.size());
        List<ScreenResponse> pageContent = screens.subList(start, end);

        return new PageImpl<>(pageContent, pageable, screens.size());
    }

}
