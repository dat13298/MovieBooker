package com.datnt.moviebooker.service;

import com.datnt.moviebooker.entity.Screen;
import com.datnt.moviebooker.entity.Seat;
import com.datnt.moviebooker.repository.SeatRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class SeatService {
    private static final Logger logger = LoggerFactory.getLogger(SeatService.class);

    private final SeatRepository seatRepository;
    private final ScreenService screenService;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private static final String SEAT_CACHE_PREFIX = "screen_seats:";

    public List<Seat> getAllSeats(Long screenId) {
        String cacheKey = SEAT_CACHE_PREFIX + screenId;
        String cachedSeats = redisTemplate.opsForValue().get(cacheKey);

        // if cache is not empty, return cached seats
        if (cachedSeats != null) {
            try {
                return objectMapper.readValue(cachedSeats, new TypeReference<List<Seat>>() {});
            } catch (Exception e) {
                logger.error("Error parsing seats from cache", e);
            }
        }

        // if cache is empty, get all seats from database and save to cache
        List<Seat> seats = seatRepository.findSeatsByScreen_Id((screenId));
        try {
            String jsonSeats = objectMapper.writeValueAsString(seats);
            redisTemplate.opsForValue().set(cacheKey, jsonSeats, 10, TimeUnit.MINUTES);
        } catch (Exception e) {
            logger.error("Error parsing seats from cache", e);
        }

        return seats;
    }

    public Seat createSeat(Seat seat) {
        Seat savedSeat = seatRepository.save(seat);
        clearSeatCache(seat.getScreen().getId());
        return savedSeat;
    }

    public Seat updateSeat(Long id, Seat updatedSeat) {
        return seatRepository.findById(id).map(seat -> {
            seat.setSeatNumber(updatedSeat.getSeatNumber());
            Screen screen = screenService.getScreenById(updatedSeat.getScreen().getId());
            seat.setScreen(screen);
            Seat savedSeat = seatRepository.save(seat);
            clearSeatCache(seat.getScreen().getId());
            return savedSeat;
        }).orElse(null);
    }

    public void deleteSeat(Long id) {
        seatRepository.findById(id).ifPresent(seat -> {
            seatRepository.deleteById(id);
            clearSeatCache(seat.getScreen().getId());
        });
    }

    public void clearSeatCache(Long screenId) {
        redisTemplate.delete(SEAT_CACHE_PREFIX + screenId);
    }
}
