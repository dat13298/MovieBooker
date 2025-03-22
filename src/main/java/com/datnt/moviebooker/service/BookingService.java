package com.datnt.moviebooker.service;

import com.datnt.moviebooker.constant.Status;
import com.datnt.moviebooker.entity.*;
import com.datnt.moviebooker.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingSeatRepository bookingSeatRepository;
    private final SeatRepository seatRepository;
    private final UserService userService;
    private final ShowTimeService showTimeService;
    private final BookingRepository bookingRepository;

    public boolean isSeatAlreadyBooked(Long seatId, Long showTimeId) {
        return bookingSeatRepository.existsBySeat_IdAndBooking_ShowTime_Id(seatId, showTimeId);
    }

    @Transactional
    public void processBooking(Long showTimeId, List<Long> seatIds, Long userId) {
        // Check seat is already booked
        seatIds.forEach(seatId -> {
            if (isSeatAlreadyBooked(seatId, showTimeId)) {
                throw new RuntimeException("Seat " + seatId + " already booked!");
            }
        });

        // Get User and ShowTime
        User user = userService.findById(userId);
        ShowTime showTime = showTimeService.findById(showTimeId);

        // Create Booking
        Booking booking = Booking.builder()
                .user(user)
                .showTime(showTime)
                .status(Status.PENDING)
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .build();

        // Save Booking to database
        booking = bookingRepository.save(booking);

        // Save BookingSeat to database
        Booking finalBooking = booking;
        seatIds.forEach(seatId -> {
            Seat seat = seatRepository.findById(seatId).orElseThrow(() -> new RuntimeException("Seat not found!"));
            BookingSeat bookingSeat = BookingSeat.builder()
                    .booking(finalBooking)
                    .seat(seat)
                    .build();
            bookingSeatRepository.save(bookingSeat);
        });
    }
}

