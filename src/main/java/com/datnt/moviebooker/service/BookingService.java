package com.datnt.moviebooker.service;

import com.datnt.moviebooker.constant.Status;
import com.datnt.moviebooker.entity.*;
import com.datnt.moviebooker.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingSeatRepository bookingSeatRepository;
    private final SeatRepository seatRepository;
    private final UserRepository userRepository;
    private final ShowTimeRepository showTimeRepository;
    private final BookingRepository bookingRepository;
    private final StringRedisTemplate redisTemplate;

    public String getBookingStatus(String bookingId) {
        return redisTemplate.opsForValue().get(bookingId);
    }

    public void saveBookingStatus(String bookingId, String status) {
        redisTemplate.opsForValue().set(bookingId, status);
    }

    public boolean isSeatAlreadyBooked(Long seatId, Long showTimeId) {
        return bookingSeatRepository.existsBySeat_IdAndBooking_ShowTime_Id(seatId, showTimeId);
    }

    @Transactional
    public void processBooking(Long showTimeId, List<Long> seatIds, Long userId) {
        // Check xem ghế đã được đặt chưa
        seatIds.forEach(seatId -> {
            if (isSeatAlreadyBooked(seatId, showTimeId)) {
                throw new RuntimeException("Seat " + seatId + " already booked!");
            }
        });

        // Lấy thông tin User và ShowTime
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found!"));
        ShowTime showTime = showTimeRepository.findById(showTimeId).orElseThrow(() -> new RuntimeException("ShowTime not found!"));

        // Tạo Booking và lưu vào database trước
        Booking booking = Booking.builder()
                .user(user)
                .showTime(showTime)
                .status(Status.PENDING)
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .build();

        // Lưu booking trước để trở thành persistent object
        booking = bookingRepository.save(booking);

        // Lưu danh sách BookingSeat
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

