package com.datnt.moviebooker.dto;

import lombok.Data;

@Data
public class LockSeatRequest {
    private Long seatId;
    private Long showTimeId;
    private Long userId;
}

