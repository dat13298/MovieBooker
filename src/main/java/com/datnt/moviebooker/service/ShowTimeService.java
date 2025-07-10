package com.datnt.moviebooker.service;

import com.datnt.moviebooker.dto.ShowTimeRequest;
import com.datnt.moviebooker.dto.ShowTimeResponse;
import com.datnt.moviebooker.entity.Movie;
import com.datnt.moviebooker.entity.Screen;
import com.datnt.moviebooker.entity.ShowTime;
import com.datnt.moviebooker.mapper.ShowTimeMapper;
import com.datnt.moviebooker.repository.MovieRepository;
import com.datnt.moviebooker.repository.ScreenRepository;
import com.datnt.moviebooker.repository.ShowTimeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShowTimeService {
    private static final Logger logger = LoggerFactory.getLogger(ShowTimeService.class);

    private final ShowTimeRepository showTimeRepository;
    private final MovieRepository movieRepository;
    private final ScreenRepository screenRepository;
    private final ShowTimeMapper showTimeMapper;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String SHOWTIME_CACHE_KEY = "showTimes";

    public Page<ShowTimeResponse> getAllShowTimes(Pageable pageable) {
        HashOperations<String, String, String> hashOps = redisTemplate.opsForHash();

        List<ShowTimeResponse> cachedShowTimes = hashOps.entries(SHOWTIME_CACHE_KEY).values().stream()
                .map(json -> {
                    try {
                        return objectMapper.readValue(json, ShowTimeResponse.class);
                    } catch (Exception e) {
                        logger.error("Error parsing show time from cache", e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (cachedShowTimes.isEmpty()) {
            List<ShowTime> showTimes = showTimeRepository.findAll();
            cachedShowTimes = showTimes.stream()
                    .map(showTimeMapper::toResponse)
                    .collect(Collectors.toList());

            showTimes.forEach(this::updateShowTimeCache);
            redisTemplate.expire(SHOWTIME_CACHE_KEY, 10, TimeUnit.MINUTES);
        }

        return paginateList(cachedShowTimes, pageable);
    }

    public void refreshShowTimeCache(Long showTimeId) {
        showTimeRepository.findById(showTimeId).ifPresent(this::updateShowTimeCache);
    }

    public ShowTimeResponse createShowTime(ShowTimeRequest request) {
        Movie movie = movieRepository.findById(request.movieId())
                .orElseThrow(() -> new RuntimeException("Movie not found!"));

        Screen screen = screenRepository.findById(request.screenId())
                .orElseThrow(() -> new RuntimeException("Screen not found!"));

        ShowTime showTime = showTimeMapper.toEntity(request, movie, screen);
        ShowTime savedShowTime = showTimeRepository.save(showTime);

        updateShowTimeCache(savedShowTime);

        return showTimeMapper.toResponse(savedShowTime);
    }

    public ShowTimeResponse updateShowTime(Long id, ShowTimeRequest request) {
        return showTimeRepository.findById(id).map(showTime -> {
            Movie movie = movieRepository.findById(request.movieId())
                    .orElseThrow(() -> new RuntimeException("Movie not found!"));

            Screen screen = screenRepository.findById(request.screenId())
                    .orElseThrow(() -> new RuntimeException("Screen not found!"));

            showTime.setMovie(movie);
            showTime.setScreen(screen);
            showTime.setStartTime(request.startTime());
            showTime.setPresentation(request.presentation());

            ShowTime updatedShowTime = showTimeRepository.save(showTime);

            clearShowTimeCache();
            return showTimeMapper.toResponse(updatedShowTime);
        }).orElseThrow(() -> new RuntimeException("ShowTime not found!"));
    }

    public void deleteShowTime(Long id) {
        if (!showTimeRepository.existsById(id)) {
            throw new RuntimeException("ShowTime not found!");
        }
        showTimeRepository.deleteById(id);
        redisTemplate.opsForHash().delete(SHOWTIME_CACHE_KEY, id.toString());
    }

    public ShowTime findEntityById(Long id) {
        return showTimeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ShowTime not found!"));
    }

    public List<ShowTimeResponse> getUpcomingShowTimesByMovie(Long movieId) {
        List<ShowTime> showTimes = showTimeRepository.findByMovieIdAndStartTimeAfter(movieId, LocalDateTime.now());

        return showTimes.stream()
                .map(showTimeMapper::toResponse)
                .collect(Collectors.toList());
    }


    public ShowTimeResponse findById(Long id) {
        HashOperations<String, String, String> hashOps = redisTemplate.opsForHash();
        String cachedShowTime = hashOps.get(SHOWTIME_CACHE_KEY, id.toString());

        if (cachedShowTime != null) {
            try {
                return objectMapper.readValue(cachedShowTime, ShowTimeResponse.class);
            } catch (Exception e) {
                logger.error("Error parsing show time from cache", e);
            }
        }

        return showTimeRepository.findById(id)
                .map(showTime -> {
                    ShowTimeResponse response = showTimeMapper.toResponse(showTime);
                    updateShowTimeCache(showTime);
                    return response;
                })
                .orElseThrow(() -> new RuntimeException("ShowTime not found!"));
    }

    private void updateShowTimeCache(ShowTime showTime) {
        try {
            ShowTimeResponse response = showTimeMapper.toResponse(showTime);
            redisTemplate.opsForHash().put(SHOWTIME_CACHE_KEY, showTime.getId().toString(), objectMapper.writeValueAsString(response));
        } catch (Exception e) {
            logger.error("Error saving show time to cache", e);
        }
    }

    private void clearShowTimeCache() {
        redisTemplate.delete(SHOWTIME_CACHE_KEY);
    }

    private Page<ShowTimeResponse> paginateList(List<ShowTimeResponse> showTimes, Pageable pageable) {
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), showTimes.size());
        return new PageImpl<>(showTimes.subList(start, end), pageable, showTimes.size());
    }
}
