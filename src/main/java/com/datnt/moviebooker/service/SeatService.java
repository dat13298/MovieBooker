package com.datnt.moviebooker.service;

import com.datnt.moviebooker.constant.SeatStatus;
import com.datnt.moviebooker.dto.SeatRequest;
import com.datnt.moviebooker.dto.SeatRequestWithId;
import com.datnt.moviebooker.dto.SeatResponse;
import com.datnt.moviebooker.entity.Screen;
import com.datnt.moviebooker.entity.Seat;
import com.datnt.moviebooker.entity.ShowTime;
import com.datnt.moviebooker.mapper.SeatMapper;
import com.datnt.moviebooker.repository.SeatRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class SeatService {

    private static final Logger log = LoggerFactory.getLogger(SeatService.class);

    private static final String SCREEN_CACHE = "screen_seats:";

    private static final String ST_CACHE     = "showtime_seats:";

    private final SeatRepository seatRepo;
    private final ScreenService screenService;
    private final StringRedisTemplate redis;
    private final ObjectMapper om;
    private final SeatMapper seatMapper;
    private final ShowTimeService showTimeService;

    public Page<SeatResponse> getAllSeats(Long screenId, Pageable pageable) {

        String key        = SCREEN_CACHE + screenId;
        String cachedJson = redis.opsForValue().get(key);

        List<Seat> seats;
        if (cachedJson != null) {
            try {
                seats = om.readValue(cachedJson, new TypeReference<>() {});
            } catch (IOException ex) {
                log.error("Corrupted seat cache – clearing {}", key, ex);
                redis.delete(key);
                seats = null;
            }
            if (seats != null) {
                return toPage(seats, pageable);
            }
        }

        Page<Seat> page = seatRepo.findSeatsByScreen_Id(screenId, pageable);
        try {
            redis.opsForValue().set(
                    key,
                    om.writeValueAsString(page.getContent()),
                    10, TimeUnit.MINUTES);
        } catch (Exception ex) {
            log.error("Cache error", ex);
        }
        return page.map(seatMapper::toResponse);
    }

    public Long getShowTimeIdBySeatId(Long seatId) {
        return seatRepo.findById(seatId)
                .map(seat -> seat.getShowTime().getId())
                .orElse(null);
    }


    @Transactional
    public void updateSeatStatus(Long seatId, SeatStatus status) {
        seatRepo.findById(seatId).ifPresent(seat -> {
            seat.setStatus(status);
            seatRepo.save(seat);
        });
    }

    @Transactional
    public void bulkUpsert(List<SeatRequestWithId> seatDtos) {
        for (SeatRequestWithId dto : seatDtos) {
            if (dto.getId() == null) {
                createSeat(convertToSeatRequest(dto));
            } else {
                updateSeat(dto.getId(), convertToSeatRequest(dto));
            }
        }
        // clear cache sau khi thao tác
        if (!seatDtos.isEmpty()) {
            Long screenId = seatDtos.get(0).getScreenId();
            Long showTimeId = seatDtos.get(0).getShowTimeId();
            clearSeatCache(screenId);
            clearShowtimeCache(showTimeId);
        }
    }

    private SeatRequest convertToSeatRequest(SeatRequestWithId dto) {
        SeatRequest req = new SeatRequest();
        req.setSeatNumber(dto.getSeatNumber());
        req.setScreenId(dto.getScreenId());
        req.setShowTimeId(dto.getShowTimeId());
        req.setPrice(dto.getPrice());
        req.setSeatType(dto.getSeatType());
        req.setStatus(dto.getStatus());
        req.setRowIdx(dto.getRowIdx());
        req.setColIdx(dto.getColIdx());
        return req;
    }


    public void clearShowtimeCache(Long showTimeId) {
        redis.delete(ST_CACHE + showTimeId);
    }

    public List<SeatResponse> findSeatsByShowTime(Long showTimeId) {

        String key     = ST_CACHE + showTimeId;
        String json    = redis.opsForValue().get(key);
        List<SeatResponse> list;

        if (json != null) {
            try {
                list = om.readValue(json, new TypeReference<>() {});
            } catch (IOException e) {
                log.error("Bad show-time cache – clearing {}", key, e);
                redis.delete(key);
                list = null;
            }
            if (list != null) {
                applyLockState(list, showTimeId);
                return list;
            }
        }

        list = seatRepo.findByShowTime_Id(showTimeId)
                .stream().map(seatMapper::toResponse).toList();

        try {
            redis.opsForValue().set(
                    key, om.writeValueAsString(list), 10, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.error("Cache error", e);
        }

        applyLockState(list, showTimeId);
        return list;
    }

    private void applyLockState(List<SeatResponse> list, Long showTimeId) {
        list.forEach(s -> {
            String lockKey = "seat_lock:" + showTimeId + ":" + s.getId();
            if (Boolean.TRUE.equals(redis.hasKey(lockKey))) {
                s.setStatus(SeatStatus.UNAVAILABLE);
            }
        });
    }

    public SeatResponse createSeat(SeatRequest req) {
        Screen screen = screenService.getScreenEntityById(req.getScreenId());
        ShowTime showTime = showTimeService.findEntityById(req.getShowTimeId());
        Seat saved = seatRepo.save(seatMapper.toEntity(req, screen, showTime));
        clearSeatCache(screen.getId());
        return seatMapper.toResponse(saved);
    }

    public SeatResponse updateSeat(Long id, SeatRequest req) {
        return seatRepo.findById(id).map(seat -> {
            Screen sc = screenService.getScreenEntityById(req.getScreenId());
            ShowTime st = showTimeService.findEntityById(req.getShowTimeId());
            seatMapper.updateEntity(seat, req, sc, st);
            Seat saved = seatRepo.save(seat);
            clearSeatCache(sc.getId());
            return seatMapper.toResponse(saved);
        }).orElseThrow(() -> new RuntimeException("Seat not found"));
    }

    public void deleteSeat(Long id) {
        seatRepo.findById(id).ifPresent(seat -> {
            seatRepo.deleteById(id);
            clearSeatCache(seat.getScreen().getId());
        });
    }

    private void clearSeatCache(Long screenId) {
        redis.delete(SCREEN_CACHE + screenId);
    }

    private Page<SeatResponse> toPage(List<Seat> seats, Pageable p) {
        int start = (int) p.getOffset();
        int end   = Math.min(start + p.getPageSize(), seats.size());
        List<SeatResponse> slice = seats.subList(start, end)
                .stream().map(seatMapper::toResponse).toList();
        return new PageImpl<>(slice, p, seats.size());
    }

    public Seat findSeatById(Long id) {
        return seatRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Seat not found"));
    }
}
