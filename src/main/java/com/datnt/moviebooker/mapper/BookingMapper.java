package com.datnt.moviebooker.mapper;

import com.datnt.moviebooker.dto.BookingResponse;
import com.datnt.moviebooker.dto.BookingSeatResponse;
import com.datnt.moviebooker.dto.ComboItemResponse;
import com.datnt.moviebooker.entity.Booking;
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

        var showTime = booking.getShowTime();
        var movie = showTime.getMovie();
        var theater = showTime.getScreen();
        String movieTitle = movie.getMovieName();
        String theaterName = theater.getName();
        String showHour = showTime.getStartTime().toString();
        List<String> seatCodes = booking.getBookingSeats().stream()
                .map(seat -> seat.getSeat().getSeatNumber())
                .collect(Collectors.toList());

        return new BookingResponse(
                booking.getId(),
                booking.getUser().getId(),
                booking.getShowTime().getId(),
                booking.getStatus(),
                booking.getCreatedAt(),
                seatResponses,
                comboResponses,
                booking.getTotalAmount(),
                movieTitle,
                theaterName,
                seatCodes,
                showHour
        );
    }
}
