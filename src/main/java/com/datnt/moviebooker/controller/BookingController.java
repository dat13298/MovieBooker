package com.datnt.moviebooker.controller;

import com.datnt.moviebooker.dto.BookingRequest;
import com.datnt.moviebooker.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bookings")
public class BookingController {

    private final AuthService authService;
    private final KafkaProducer kafkaProducer;
    private final StringRedisTemplate redisTemplate;

    @PostMapping("/create")
    public ResponseEntity<?> createBooking(@RequestBody BookingRequest request) {
        Long userId = authService.getCurrentUserId();

        // send information to kafka for booking
        String bookingId = kafkaProducer.sendSeatBookingRequest(request.getShowTimeId(), request.getSeatIds(), userId);

        // wait kafka process in Redis
        String result = null;
        for (int i = 0; i < 30; i++) {
            result = redisTemplate.opsForValue().get(bookingId);
            if (result != null) break;
            try {
                Thread.sleep(100); // wait 100ms per times
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        if (result == null) {
            return ResponseEntity.status(500).body("Kafka timeout!");
        }

        return ResponseEntity.ok(result);
    }


    @GetMapping("/status/{bookingId}")
    public ResponseEntity<?> getBookingStatus(@PathVariable String bookingId) {
        String result = redisTemplate.opsForValue().get(bookingId);
        if (result == null) {
            return ResponseEntity.ok("Booking is being processed...");
        }
        return ResponseEntity.ok("Booking Status: " + result);
    }
}
