package com.datnt.moviebooker.mapper;

import com.datnt.moviebooker.dto.BookingResponse;
import com.datnt.moviebooker.dto.BookingSeatResponse;
import com.datnt.moviebooker.dto.ComboItemResponse;
import com.datnt.moviebooker.entity.Booking;
import com.datnt.moviebooker.mapper.ComboItemMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BookingMapper {

    private final ComboItemMapper comboItemMapper;

    public BookingResponse toResponse(Booking booking) {
        List<BookingSeatResponse> seatResponses = booking.getBookingSeats() != null
                ? booking.getBookingSeats().stream()
                .map(seat -> new BookingSeatResponse(seat.getId(), seat.getSeat().getId()))
                .collect(Collectors.toList())
                : new ArrayList<>();

        List<ComboItemResponse> comboResponses = booking.getFoodBookings() != null
                ? booking.getFoodBookings().stream()
                .map(comboItemMapper::toResponse)
                .collect(Collectors.toList())
                : new ArrayList<>();

        return new BookingResponse(
                booking.getId(),
                booking.getUser().getId(),
                booking.getShowTime().getId(),
                booking.getStatus(),
                booking.getCreatedAt(),
                seatResponses,
                comboResponses,
                booking.getTotalAmount()
        );
    }
}
