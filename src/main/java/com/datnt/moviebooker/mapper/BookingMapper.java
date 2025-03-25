package com.datnt.moviebooker.mapper;

import com.datnt.moviebooker.dto.BookingResponse;
import com.datnt.moviebooker.dto.BookingSeatResponse;
import com.datnt.moviebooker.entity.Booking;
import org.springframework.stereotype.Component;
import java.util.stream.Collectors;

@Component
public class BookingMapper {

    public BookingResponse toResponse(Booking booking) {
        return new BookingResponse(
                booking.getId(),
                booking.getUser().getId(),
                booking.getShowTime().getId(),
                booking.getStatus(),
                booking.getCreatedAt(),
                booking.getBookingSeats().stream()
                        .map(seat -> new BookingSeatResponse(seat.getId(), seat.getSeat().getId()))
                        .collect(Collectors.toList())
        );
    }
}
