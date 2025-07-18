package com.datnt.moviebooker.service;

import com.datnt.moviebooker.constant.SeatStatus;
import com.datnt.moviebooker.constant.Status;
import com.datnt.moviebooker.dto.BookingResponse;
import com.datnt.moviebooker.dto.ComboItemRequest;
import com.datnt.moviebooker.entity.*;
import com.datnt.moviebooker.mapper.BookingMapper;
import com.datnt.moviebooker.mapper.ComboItemMapper;
import com.datnt.moviebooker.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingSeatRepository bookingSeatRepository;
    private final SeatRepository seatRepository;
    private final UserService userService;
    private final ShowTimeService showTimeService;
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final ComboFoodRepository comboFoodRepository;
    private final FoodBookingRepository foodBookingRepository;
    private final ComboItemMapper comboItemMapper;
    private final PaymentRepository paymentRepository;

    public boolean isSeatAlreadyBooked(Long seatId, Long showTimeId) {
        return bookingSeatRepository.existsBySeat_IdAndBooking_ShowTime_Id(seatId, showTimeId);
    }

    @Transactional
    public void processBooking(Long showTimeId, List<Long> seatIds, Long userId) {
        // check seat is already booked or not
        seatIds.forEach(seatId -> {
            if (isSeatAlreadyBooked(seatId, showTimeId)) {
                throw new RuntimeException("Seat " + seatId + " already booked!");
            }
        });

        // get User and ShowTime
        User user = userService.findById(userId);
        ShowTime showTime = showTimeService.findEntityById(showTimeId);

        Booking booking = Booking.builder()
                .user(user)
                .showTime(showTime)
                .status(Status.PENDING)
                .bookingCode(generateBookingCode())
                .build();
        booking = bookingRepository.save(booking);// save Booking

        // save BookingSeat
        Booking finalBooking = booking;
        seatIds.forEach(seatId -> {
            Seat seat = seatRepository.findById(seatId).orElseThrow(() -> new RuntimeException("Seat not found!"));
            BookingSeat bookingSeat = BookingSeat.builder()
                    .booking(finalBooking)
                    .seat(seat)
                    .price(seat.getPrice())
                    .build();
            bookingSeatRepository.save(bookingSeat);
        });

        bookingMapper.toResponse(booking);
    }

    public Optional<BookingResponse> getBookingById(Long bookingId) {
        return bookingRepository.findById(bookingId).map(bookingMapper::toResponse);
    }

    public Page<BookingResponse> getAllBookings(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return bookingRepository.findAll(pageable).map(bookingMapper::toResponse);
    }

    @Transactional
    public void cancelBooking(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found!"));

        if (!booking.getUser().getId().equals(userId) && !userService.isAdmin(userId)) {
            throw new RuntimeException("You do not have permission to cancel this booking!");
        }

        paymentRepository.deleteByBookingId(bookingId);

        foodBookingRepository.deleteByBooking_Id(bookingId);

        // Delete BookingSeat first
        bookingSeatRepository.deleteByBooking_Id(bookingId);

        // Delete Booking
        bookingRepository.delete(booking);
    }

    public Booking createPendingBooking(Long showTimeId, List<Long> seatIds, List<ComboItemRequest> combos, Long userId) {
        ShowTime showTime = showTimeService.findEntityById(showTimeId);
        User user = userService.findById(userId);

        List<Seat> seats = seatRepository.findAllById(seatIds);
        long totalSeats = seats.stream().mapToLong(Seat::getPrice).sum();

        long totalCombos = 0;
        List<FoodBooking> foodBookings = new ArrayList<>();

        for (ComboItemRequest c : combos) {
            ComboFood combo = comboFoodRepository.findById(c.comboId())
                    .orElseThrow(() -> new RuntimeException("Combo not found: " + c.comboId()));
            FoodBooking fb = comboItemMapper.toEntity(c, combo, null);
            foodBookings.add(fb);
            totalCombos += combo.getPrice() * c.quantity();
        }

        Booking booking = Booking.builder()
                .user(user)
                .showTime(showTime)
                .status(Status.PENDING)
                .bookingCode(generateBookingCode())
                .totalAmount(totalSeats + totalCombos)
                .build();

        booking = bookingRepository.save(booking);

        // ✅ Ghi ghế vào booking_seat
        List<BookingSeat> bookingSeats = new ArrayList<>();
        for (Seat seat : seats) {
            BookingSeat bs = BookingSeat.builder()
                    .booking(booking)
                    .seat(seat)
                    .price(seat.getPrice())
                    .build();
            bookingSeats.add(bs);
        }
        bookingSeatRepository.saveAll(bookingSeats);

        for (FoodBooking fb : foodBookings) {
            fb.setBooking(booking);
        }
        foodBookingRepository.saveAll(foodBookings);

        return booking;
    }



    @Transactional
    public void confirmBookingAndSaveSeats(Long bookingId, Long showTimeId, List<Long> seatIds) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        booking.setStatus(Status.CONFIRMED);
        bookingRepository.save(booking);

        for (Long seatId : seatIds) {
            Seat seat = seatRepository.findById(seatId)
                    .orElseThrow(() -> new RuntimeException("Seat not found: " + seatId));

            seat.setStatus(SeatStatus.UNAVAILABLE);
            seatRepository.save(seat);

            BookingSeat bs = BookingSeat.builder()
                    .booking(booking)
                    .seat(seat)
                    .price(seat.getPrice())
                    .build();
            bookingSeatRepository.save(bs);
        }
    }

    @Transactional
    public void finalizeBookingAfterPayment(Long bookingId, boolean isSuccess) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (isSuccess) {
            booking.setStatus(Status.SUCCESS);
            bookingRepository.save(booking);

            List<BookingSeat> bookingSeats = bookingSeatRepository.findByBooking_Id(bookingId);
            for (BookingSeat bs : bookingSeats) {
                Seat seat = bs.getSeat();
                seat.setStatus(SeatStatus.BOOKED);
                seatRepository.save(seat);
            }

        } else {
            List<BookingSeat> bookingSeats = bookingSeatRepository.findByBooking_Id(bookingId);
            for (BookingSeat bs : bookingSeats) {
                Seat seat = bs.getSeat();
                seat.setStatus(SeatStatus.AVAILABLE);
                seatRepository.save(seat);
            }

            // Xóa các quan hệ phụ
            paymentRepository.deleteByBookingId(bookingId);
            foodBookingRepository.deleteByBooking_Id(bookingId);
            bookingSeatRepository.deleteByBooking_Id(bookingId);

            // Xóa booking
            bookingRepository.delete(booking);
        }
    }

    public List<BookingResponse> getBookingsByUser(Long userId) {
        List<Booking> bookings = bookingRepository.findByUserIdWithDetails(userId);
        return bookings.stream()
                .map(bookingMapper::toResponse)
                .toList();
    }

    private String generateBookingCode() {
        return "BK" + java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
