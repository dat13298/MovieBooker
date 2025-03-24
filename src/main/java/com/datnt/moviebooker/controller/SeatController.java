package com.datnt.moviebooker.controller;

import com.datnt.moviebooker.dto.LockSeatRequest;
import com.datnt.moviebooker.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/seats")
@RequiredArgsConstructor
public class SeatController {
    private final RedisService redisService;

    @PostMapping("/lock")
    public ResponseEntity<?> lockSeat(@RequestBody LockSeatRequest request) {
        // Lock the seat for 5 minutes
        boolean locked = redisService.tryLockSeat(
                request.getSeatId(),
                request.getShowTimeId(),
                request.getUserId(),
                5, TimeUnit.MINUTES
        );

        if (locked) {
            return ResponseEntity.ok("Seat locked successfully");
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Seat already locked");
        }
    }
}
