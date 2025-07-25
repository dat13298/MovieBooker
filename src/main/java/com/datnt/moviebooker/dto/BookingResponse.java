package com.datnt.moviebooker.dto;

import com.datnt.moviebooker.constant.Status;
import java.time.LocalDateTime;
import java.util.List;

public record BookingResponse(
        Long id,
        Long userId,
        Long showTimeId,
        Status status,
        LocalDateTime createdAt,
        List<BookingSeatResponse> seats,
        List<ComboItemResponse> combos,
        Long totalAmount,
        String movieTitle,
        String theaterName,
        List<String> seatCodes,
        String showTime,
        String bookingCode
) {}

