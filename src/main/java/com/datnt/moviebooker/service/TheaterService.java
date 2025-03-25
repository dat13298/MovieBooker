package com.datnt.moviebooker.service;

import com.datnt.moviebooker.dto.TheaterRequest;
import com.datnt.moviebooker.dto.TheaterResponse;
import com.datnt.moviebooker.entity.Theater;
import com.datnt.moviebooker.mapper.TheaterMapper;
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
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TheaterService {
    private static final Logger logger = LoggerFactory.getLogger(TheaterService.class);

    private final TheaterRepository theaterRepository;
    private final TheaterMapper theaterMapper;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private static final String THEATER_CACHE_KEY = "theaters";

    public Page<TheaterResponse> getAllTheaters(Pageable pageable) {
        HashOperations<String, String, String> hashOps = redisTemplate.opsForHash();

        List<TheaterResponse> cachedTheaters = hashOps.entries(THEATER_CACHE_KEY).values().stream()
                .map(json -> {
                    try {
                        return objectMapper.readValue(json, TheaterResponse.class);
                    } catch (Exception e) {
                        logger.error("Error parsing theater from cache", e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();

        if (cachedTheaters.isEmpty()) {
            List<TheaterResponse> theaters = theaterRepository.findAll()
                    .stream()
                    .map(theaterMapper::toResponse)
                    .toList();

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

    public Optional<TheaterResponse> getTheaterById(Long id) {
        HashOperations<String, String, String> hashOps = redisTemplate.opsForHash();
        String cachedTheater = hashOps.get(THEATER_CACHE_KEY, id.toString());

        if (cachedTheater != null) {
            try {
                return Optional.of(objectMapper.readValue(cachedTheater, TheaterResponse.class));
            } catch (Exception e) {
                logger.error("Error parsing theater from cache", e);
            }
        }

        return theaterRepository.findById(id).map(theater -> {
            TheaterResponse response = theaterMapper.toResponse(theater);
            try {
                hashOps.put(THEATER_CACHE_KEY, id.toString(), objectMapper.writeValueAsString(response));
            } catch (Exception e) {
                logger.error("Error saving theater to cache", e);
            }
            return response;
        });
    }

    public TheaterResponse createTheater(TheaterRequest theaterRequest) {
        Theater theater = theaterMapper.toEntity(theaterRequest);
        Theater savedTheater = theaterRepository.save(theater);
        TheaterResponse response = theaterMapper.toResponse(savedTheater);
        updateTheaterCache(response);
        return response;
    }

    public TheaterResponse updateTheater(Long id, TheaterRequest theaterRequest) {
        return theaterRepository.findById(id).map(theater -> {
            theaterMapper.updateEntity(theater, theaterRequest);
            Theater updatedTheater = theaterRepository.save(theater);
            TheaterResponse response = theaterMapper.toResponse(updatedTheater);
            updateTheaterCache(response);
            return response;
        }).orElseThrow(() -> new RuntimeException("Theater not found"));
    }

    public void deleteTheater(Long id) {
        theaterRepository.deleteById(id);
        redisTemplate.opsForHash().delete(THEATER_CACHE_KEY, id.toString());
    }

    private void updateTheaterCache(TheaterResponse theater) {
        try {
            redisTemplate.opsForHash().put(THEATER_CACHE_KEY, theater.getId().toString(), objectMapper.writeValueAsString(theater));
            redisTemplate.expire(THEATER_CACHE_KEY, 10, TimeUnit.MINUTES);
        } catch (Exception e) {
            logger.error("Error saving theater to cache", e);
        }
    }

    private Page<TheaterResponse> paginateList(List<TheaterResponse> theaters, Pageable pageable) {
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), theaters.size());
        return new PageImpl<>(theaters.subList(start, end), pageable, theaters.size());
    }
}
