package com.datnt.moviebooker.service;

import com.datnt.moviebooker.dto.SeatRequest;
import com.datnt.moviebooker.dto.SeatResponse;
import com.datnt.moviebooker.entity.Screen;
import com.datnt.moviebooker.entity.Seat;
import com.datnt.moviebooker.mapper.SeatMapper;
import com.datnt.moviebooker.repository.SeatRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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
    private final SeatMapper seatMapper;
    private static final String SEAT_CACHE_PREFIX = "screen_seats:";

    public Page<SeatResponse> getAllSeats(Long screenId, Pageable pageable) {
        String cacheKey = SEAT_CACHE_PREFIX + screenId;
        String cachedSeats = redisTemplate.opsForValue().get(cacheKey);

        if (cachedSeats != null && !cachedSeats.isEmpty()) {
            try {
                List<Seat> seats = objectMapper.readValue(cachedSeats, new TypeReference<List<Seat>>() {});
                if (!seats.isEmpty()) {
                    return toPage(seats, pageable);
                }
            } catch (Exception e) {
                logger.error("Error parsing seats from cache, clearing cache...", e);
                clearSeatCache(screenId);
            }
        }


        Page<Seat> seatPage = seatRepository.findSeatsByScreen_Id(screenId, pageable);
        try {
            redisTemplate.opsForValue().set(cacheKey, objectMapper.writeValueAsString(seatPage.getContent()), 10, TimeUnit.MINUTES);
        } catch (Exception e) {
            logger.error("Error caching seats", e);
        }

        return seatPage.map(seatMapper::toResponse);
    }

    // Convert List to Page manually (for cache data)
    private Page<SeatResponse> toPage(List<Seat> seats, Pageable pageable) {
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), seats.size());

        List<SeatResponse> seatResponses = seats.subList(start, end).stream()
                .map(seatMapper::toResponse)
                .toList();

        return new PageImpl<>(seatResponses, pageable, seats.size());
    }


    public SeatResponse createSeat(SeatRequest request) {
        Screen screen = screenService.getScreenEntityById(request.getScreenId());
        Seat seat = seatMapper.toEntity(request, screen);
        Seat savedSeat = seatRepository.save(seat);
        clearSeatCache(screen.getId());
        return seatMapper.toResponse(savedSeat);
    }


    public SeatResponse updateSeat(Long id, SeatRequest request) {
        return seatRepository.findById(id).map(seat -> {
            Screen screen = screenService.getScreenEntityById(request.getScreenId());
            seatMapper.updateEntity(seat, request, screen);
            Seat savedSeat = seatRepository.save(seat);
            clearSeatCache(screen.getId());
            return seatMapper.toResponse(savedSeat);
        }).orElseThrow(() -> new RuntimeException("Seat not found!"));
    }

    public SeatResponse getSeatById(Long id) {
        return seatRepository.findById(id)
                .map(seatMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Seat not found!"));
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
