package com.datnt.moviebooker.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SeatEvent {
    private String type;
    private Long   seatId;
    private Long   showTimeId;
    private Long   userId;
    private Long   expiresAt;
    private Long   bookingId;
}

