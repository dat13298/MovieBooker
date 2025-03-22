package com.datnt.moviebooker.controller;

import com.datnt.moviebooker.dto.BookingMessage;
import com.datnt.moviebooker.dto.BookingRequest;
import com.datnt.moviebooker.kafka.KafkaProducer;
import com.datnt.moviebooker.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bookings")
public class BookingController {

    private final AuthService authService;
    private final KafkaProducer kafkaProducer;
    private final RedisService redisService;

    @PostMapping("/create")
    public ResponseEntity<?> createBooking(@RequestBody BookingRequest request) {
        Long userId = authService.getCurrentUserId();
        String bookingId = UUID.randomUUID().toString();  // Tạo bookingId duy nhất

        // Create message
        BookingMessage message = new BookingMessage();
        message.setBookingId(bookingId);
        message.setUserId(userId);
        message.setShowTimeId(request.showTimeId());
        message.setSeatIds(request.seatIds());

        kafkaProducer.sendBookingMessage(message);

        return ResponseEntity.ok(bookingId);
    }


    @GetMapping("/status/{bookingId}")
    public ResponseEntity<?> getBookingStatus(@PathVariable String bookingId) {
        String result = redisService.getData(bookingId);
        if (result == null) {
            return ResponseEntity.ok("Booking is being processed...");
        }
        return ResponseEntity.ok("Booking Status: " + result);
    }
}
