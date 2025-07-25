package com.datnt.moviebooker.controller;

import com.datnt.moviebooker.dto.BookingMessage;
import com.datnt.moviebooker.dto.BookingRequest;
import com.datnt.moviebooker.dto.BookingResponse;
import com.datnt.moviebooker.entity.Booking;
import com.datnt.moviebooker.kafka.KafkaProducer;
import com.datnt.moviebooker.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bookings")
public class BookingController {

    private final AuthService authService;
    private final BookingService bookingService;
    private final KafkaProducer kafkaProducer;
    private final RedisService redisService;

    @PostMapping("/create")
    public ResponseEntity<?> createBooking(@RequestBody @Valid BookingRequest request) {
        Long userId = authService.getCurrentUserId();
        String username = authService.getCurrentUsername();

        for (Long seatId : request.seatIds()) {
            String lockedBy = redisService.getData("seat_lock:" + request.showTimeId() + ":" + seatId);
            if (lockedBy == null || !lockedBy.equals(username)) {
                return ResponseEntity.badRequest().body("Seat " + seatId + " is already booked or not locked!");
            }
        }

        Booking booking = bookingService.createPendingBooking(
                request.showTimeId(),
                request.seatIds(),
                request.comboItems(),
                userId
        );

        BookingMessage message = new BookingMessage();
        message.setBookingId(String.valueOf(booking.getId()));
        message.setUserId(userId);
        message.setShowTimeId(request.showTimeId());
        message.setSeatIds(request.seatIds());

        kafkaProducer.sendBookingMessage(message);

        redisService.saveData(String.valueOf(booking.getId()), "PENDING", 60, TimeUnit.SECONDS);

        return ResponseEntity.ok(Map.of(
                "bookingId", booking.getId(),
                "amount", booking.getTotalAmount()
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getBooking(@PathVariable Long id) {
        Optional<BookingResponse> bookingResponse = bookingService.getBookingById(id);
        return bookingResponse.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/my")
    public ResponseEntity<List<BookingResponse>> getMyBookings() {
        Long userId = authService.getCurrentUserId();
        return ResponseEntity.ok(bookingService.getBookingsByUser(userId));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<String> cancelBooking(@PathVariable Long id) {
        Long userId = authService.getCurrentUserId();
        bookingService.cancelBooking(id, userId);
        return ResponseEntity.ok("Booking cancelled successfully.");
    }


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<Page<BookingResponse>> getAllBookings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Page<BookingResponse> bookings = bookingService.getAllBookings(page, size);
        return ResponseEntity.ok(bookings);
    }





    @GetMapping("/status/{bookingId}")
    public ResponseEntity<?> getBookingStatus(@PathVariable String bookingId) {
        // get booking status from Redis
        String result = redisService.getData(bookingId);
        if (result == null) {
            return ResponseEntity.ok("Booking is being processed...");
        }
        return ResponseEntity.ok("Booking Status: " + result);
    }
}
