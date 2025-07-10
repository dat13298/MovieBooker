package com.datnt.moviebooker.controller;

import com.datnt.moviebooker.dto.LockSeatRequest;
import com.datnt.moviebooker.dto.SeatRequest;
import com.datnt.moviebooker.dto.SeatResponse;
import com.datnt.moviebooker.dto.UnlockSeatRequest;
import com.datnt.moviebooker.service.AuthService;
import com.datnt.moviebooker.service.RedisService;
import com.datnt.moviebooker.service.SeatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/seats")
@RequiredArgsConstructor
public class SeatController {
    private final RedisService redisService;
    private final SeatService seatService;
    private final AuthService authService;

    @PostMapping("/lock")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> lockSeat(@RequestBody LockSeatRequest req) {
        System.out.println("Locking seat: " + req.getSeatId() + " for show time: " + req.getShowTimeId());
        Long userId = authService.getCurrentUserId();

        boolean locked = redisService.tryLockSeat(
                req.getSeatId(),
                req.getShowTimeId(),
                userId,
                10, TimeUnit.MINUTES);

        return locked
                ? ResponseEntity.ok("Seat locked")
                : ResponseEntity.status(HttpStatus.CONFLICT)
                .body("Seat was locked by another user");
    }

    @PostMapping("/unlock")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> unlockSeat(@RequestBody UnlockSeatRequest req) {
        redisService.releaseSeat(req.getSeatId(), req.getShowTimeId());
        return ResponseEntity.ok("Seat released");
    }


    @GetMapping
    public ResponseEntity<Page<SeatResponse>> getAllSeats(
            @RequestParam Long screenId, Pageable pageable) {
        return ResponseEntity.ok(seatService.getAllSeats(screenId, pageable));
    }

//    @GetMapping("/{id}")
//    public ResponseEntity<SeatResponse> getSeatById(@PathVariable Long id) {
//        return ResponseEntity.ok(seatService.getSeatById(id));
//    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<SeatResponse> createSeat(@Valid @RequestBody SeatRequest request) {
        return ResponseEntity.ok(seatService.createSeat(request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<SeatResponse> updateSeat(
            @PathVariable Long id, @Valid @RequestBody SeatRequest request) {
        return ResponseEntity.ok(seatService.updateSeat(id, request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSeat(@PathVariable Long id) {
        seatService.deleteSeat(id);
        return ResponseEntity.noContent().build();
    }
}
