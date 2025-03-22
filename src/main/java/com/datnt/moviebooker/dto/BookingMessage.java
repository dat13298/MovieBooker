package com.datnt.moviebooker.dto;

import lombok.Data;

import java.util.List;

@Data
public class BookingMessage {
    private String bookingId;
    private Long showTimeId;
    private List<Long> seatIds;
    private Long userId;
}
