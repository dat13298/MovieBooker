package com.datnt.moviebooker.service;

import com.datnt.moviebooker.entity.Screen;
import com.datnt.moviebooker.repository.ScreenRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private final ScreenRepository screenRepository;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private static final String SCREEN_CACHE_KEY = "screens";

    public List<Screen> getAllScreens() {
        HashOperations<String, String, String> hashOps = redisTemplate.opsForHash();
        List<Screen> screens = hashOps.entries(SCREEN_CACHE_KEY).values().stream()
                .map(json -> {
                    try {
                        return objectMapper.readValue(json, Screen.class);
                    } catch (Exception e) {
                        logger.error("Error parsing screen from cache", e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();

        if (screens.isEmpty()) {
            screens = screenRepository.findAll();
            screens.forEach(screen -> {
                try {
                    hashOps.put(SCREEN_CACHE_KEY, screen.getId().toString(), objectMapper.writeValueAsString(screen));
                } catch (Exception e) {
                    logger.error("Error parsing screen from cache", e);
                }
            });
            redisTemplate.expire(SCREEN_CACHE_KEY, 10, TimeUnit.MINUTES);
        }
        return screens;
    }

    public Screen createScreen(Screen screen) {
        Screen savedScreen = screenRepository.save(screen);
        clearScreenCache();
        return savedScreen;
    }

    public Screen updateScreen(Long id, Screen updatedScreen) {
        return screenRepository.findById(id).map(screen -> {
            screen.setTheater(updatedScreen.getTheater());
            screen.setName(updatedScreen.getName());
            Screen savedScreen = screenRepository.save(screen);
            clearScreenCache();
            return savedScreen;
        }).orElse(null);
    }

    public void deleteScreen(Long id) {
        screenRepository.deleteById(id);
        clearScreenCache();
    }

    private void clearScreenCache() {
        redisTemplate.delete(SCREEN_CACHE_KEY);
    }

    public Screen getScreenById(Long id) {
        HashOperations<String, String, String> hashOps = redisTemplate.opsForHash();
        String cachedScreen = hashOps.get(SCREEN_CACHE_KEY, id.toString());

        if (cachedScreen != null) {
            try {
                return objectMapper.readValue(cachedScreen, Screen.class);
            } catch (Exception e) {
                logger.error("Error parsing cached screen data for ID {}: {}", id, e.getMessage(), e);
            }
        }

        return screenRepository.findById(id).map(screen -> {
            try {
                hashOps.put(SCREEN_CACHE_KEY, id.toString(), objectMapper.writeValueAsString(screen));
            } catch (Exception e) {
                logger.error("Error caching screen data for ID {}: {}", id, e.getMessage(), e);
            }
            return screen;
        }).orElse(null);
    }
}
