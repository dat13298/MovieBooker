package com.datnt.moviebooker.dto;

import lombok.Data;

@Data
public class UnlockSeatRequest {
    private Long seatId;
    private Long showTimeId;
}
