package com.datnt.moviebooker.controller;

import com.datnt.moviebooker.dto.BookingMessage;
import com.datnt.moviebooker.dto.BookingRequest;
import com.datnt.moviebooker.dto.BookingResponse;
import com.datnt.moviebooker.entity.Booking;
import com.datnt.moviebooker.kafka.KafkaProducer;
import com.datnt.moviebooker.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bookings")
public class BookingController {

    private final AuthService authService;
    private final BookingService bookingService;
    private final KafkaProducer kafkaProducer;
    private final RedisService redisService;

    @PostMapping("/create")
    public ResponseEntity<?> createBooking(@RequestBody BookingRequest request) {
        Long userId = authService.getCurrentUserId();
        String bookingId = UUID.randomUUID().toString();  // create bookingId for tracking booking status

        // check if seat is already booked or not locked
        for (Long seatId : request.seatIds()) {
            String lockedBy = redisService.getData("seat_lock:" + request.showTimeId() + ":" + seatId);
            if (lockedBy == null || !lockedBy.equals(userId.toString())) {
                return ResponseEntity.badRequest().body("Seat " + seatId + " is already booked or not locked!");
            }
        }

        Booking booking = bookingService.createPendingBooking(request.showTimeId(), request.seatIds(), request.comboItems(), userId);

        // create message and send to Kafka
        BookingMessage message = new BookingMessage();
        message.setBookingId(bookingId);
        message.setUserId(userId);
        message.setShowTimeId(request.showTimeId());
        message.setSeatIds(request.seatIds());

        kafkaProducer.sendBookingMessage(message);

        // Trả về bookingId và totalAmount để gọi API VNPAY redirect
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
    public ResponseEntity<Page<BookingResponse>> getMyBookings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Long userId = authService.getCurrentUserId();
        Page<BookingResponse> bookings = bookingService.getBookingsByUser(userId, page, size);

        return ResponseEntity.ok(bookings);
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
