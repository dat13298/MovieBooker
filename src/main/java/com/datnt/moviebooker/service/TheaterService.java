package com.datnt.moviebooker.service;

import com.datnt.moviebooker.entity.Theater;
import com.datnt.moviebooker.repository.TheaterRepository;
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

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TheaterService {
    private static final Logger logger = LoggerFactory.getLogger(TheaterService.class);

    private final TheaterRepository theaterRepository;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private static final String THEATER_CACHE_KEY = "theaters";

    public Page<Theater> getAllTheaters(Pageable pageable) {
        HashOperations<String, String, String> hashOps = redisTemplate.opsForHash();
        // get all theaters from cache
        List<Theater> cachedTheaters = hashOps.entries(THEATER_CACHE_KEY).values().stream()
                .map(json -> {
                    try {
                        return objectMapper.readValue(json, Theater.class);
                    } catch (Exception e) {
                        logger.error("Error parsing theater from cache", e);
                        return null;
                    }
                }).filter(Objects::nonNull)
                .toList();

        // if cache is empty, get all theaters from database and save to cache
        if (cachedTheaters.isEmpty()) {
            List<Theater> theaters = theaterRepository.findAll();
            theaters.forEach(theater -> {
                try {
                    hashOps.put(THEATER_CACHE_KEY, theater.getId().toString(), objectMapper.writeValueAsString(theater));
                } catch (Exception e) {
                    logger.error("Error saving theater to cache", e);
                }
            });
            redisTemplate.expire(THEATER_CACHE_KEY, 10, TimeUnit.MINUTES);
            cachedTheaters = theaters;
        }

        return paginateList(cachedTheaters, pageable);
    }

    public Theater createTheater(Theater theater) {
        Theater savedTheater = theaterRepository.save(theater);
        updateTheaterCache(savedTheater);// save to cache
        return savedTheater;
    }

    public Theater updateTheater(Long id, Theater updatedTheater) {
        return theaterRepository.findById(id).map(theater -> {
            theater.setName(updatedTheater.getName());
            theater.setLocation(updatedTheater.getLocation());
            Theater savedTheater = theaterRepository.save(theater);
            updateTheaterCache(savedTheater);// save to cache
            return savedTheater;
        }).orElse(null);
    }

    public void deleteTheater(Long id) {
        theaterRepository.deleteById(id);
        HashOperations<String, String, String> hashOps = redisTemplate.opsForHash();
        hashOps.delete(THEATER_CACHE_KEY, id.toString());
    }

    public Theater getTheaterById(Long id) {
        HashOperations<String, String, String> hashOps = redisTemplate.opsForHash();
        String cachedTheater = hashOps.get(THEATER_CACHE_KEY, id.toString());

        if (cachedTheater != null) {
            try {
                return objectMapper.readValue(cachedTheater, Theater.class);
            } catch (Exception e) {
                logger.error("Error parsing theater from cache", e);
            }
        }

        return theaterRepository.findById(id).map(theater -> {
            try {
                hashOps.put(THEATER_CACHE_KEY, id.toString(), objectMapper.writeValueAsString(theater));
            } catch (Exception e) {
                logger.error("Error saving theater to cache", e);
            }
            return theater;
        }).orElse(null);
    }


    private void updateTheaterCache(Theater theater) {
        HashOperations<String, String, String> hashOps = redisTemplate.opsForHash();
        try {
            hashOps.put(THEATER_CACHE_KEY, theater.getId().toString(), objectMapper.writeValueAsString(theater));
        } catch (Exception e) {
            logger.error("Error saving theater to cache", e);
        }
    }

    private Page<Theater> paginateList(List<Theater> cachedTheaters, Pageable pageable) {
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), cachedTheaters.size());
        return new PageImpl<>(cachedTheaters.subList(start, end), pageable, cachedTheaters.size());
    }
}
