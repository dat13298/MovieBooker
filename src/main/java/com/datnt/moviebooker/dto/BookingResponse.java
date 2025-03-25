package com.datnt.moviebooker.dto;

import com.datnt.moviebooker.constant.Status;
import java.sql.Timestamp;
import java.util.List;

public record BookingResponse(
        Long id,
        Long userId,
        Long showTimeId,
        Status status,
        Timestamp createdAt,
        List<BookingSeatResponse> seats
) {}
