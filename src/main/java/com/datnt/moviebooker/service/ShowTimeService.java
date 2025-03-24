package com.datnt.moviebooker.service;

import com.datnt.moviebooker.entity.ShowTime;
import com.datnt.moviebooker.repository.ShowTimeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShowTimeService {
    private final ShowTimeRepository showTimeRepository;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private static final String SHOWTIME_CACHE_KEY = "showTimes";

    public Page<ShowTime> getAllShowTimes(Pageable pageable) {
        HashOperations<String, String, String> hashOps = redisTemplate.opsForHash();
        // get all show times from cache
        List<ShowTime> cachedShowTimes = hashOps.entries(SHOWTIME_CACHE_KEY).values().stream()
                .map(json -> {
                    try {
                        return objectMapper.readValue(json, ShowTime.class);
                    } catch (Exception e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        // if cache is empty, get all show times from database and save to cache
        if (cachedShowTimes.isEmpty()) {
            List<ShowTime> showTimes = showTimeRepository.findAll();
            showTimes.forEach(showTime -> {
                try {
                    hashOps.put(SHOWTIME_CACHE_KEY, showTime.getId().toString(), objectMapper.writeValueAsString(showTime));
                } catch (Exception ignored) {}
            });
            redisTemplate.expire(SHOWTIME_CACHE_KEY, 10, TimeUnit.MINUTES);
            cachedShowTimes = showTimes;
        }

        return paginateList(cachedShowTimes, pageable);
    }

    public ShowTime createShowTime(ShowTime showTime) {
        ShowTime savedShowTime = showTimeRepository.save(showTime);
        updateShowTimeCache(savedShowTime);// save to cache
        return savedShowTime;
    }

    public ShowTime updateShowTime(Long id, ShowTime updatedShowTime) {
        return showTimeRepository.findById(id).map(showTime -> {
            showTime.setMovie(updatedShowTime.getMovie());
            showTime.setStartTime(updatedShowTime.getStartTime());
            ShowTime savedShowTime = showTimeRepository.save(showTime);
            updateShowTimeCache(savedShowTime);// save to cache
            return savedShowTime;
        }).orElse(null);
    }

    public void deleteShowTime(Long id) {
        showTimeRepository.deleteById(id);
        redisTemplate.opsForHash().delete(SHOWTIME_CACHE_KEY, id.toString());// delete from cache
    }

    private void updateShowTimeCache(ShowTime showTime) {
        try {
            // save to cache
            redisTemplate.opsForHash().put(SHOWTIME_CACHE_KEY, showTime.getId().toString(), objectMapper.writeValueAsString(showTime));
            redisTemplate.expire(SHOWTIME_CACHE_KEY, 10, TimeUnit.MINUTES);
        } catch (Exception ignored) {}
    }

    private Page<ShowTime> paginateList(List<ShowTime> showTimes, Pageable pageable) {
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), showTimes.size());
        return new PageImpl<>(showTimes.subList(start, end), pageable, showTimes.size());// paginate list
    }
}