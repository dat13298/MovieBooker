package com.datnt.moviebooker.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SeatResponse {
    private Long id;
    private String seatNumber;
    private Long screenId;
}

