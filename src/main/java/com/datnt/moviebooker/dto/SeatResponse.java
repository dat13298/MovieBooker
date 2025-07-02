package com.datnt.moviebooker.dto;

import com.datnt.moviebooker.constant.SeatStatus;
import com.datnt.moviebooker.constant.SeatType;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SeatResponse {
    private Long id;
    private String seatNumber;
    private Long screenId;
    private Long showTimeId;
    private SeatStatus status;
    private Long price;
    private SeatType seatType;
    private Integer rowIdx;
    private Integer colIdx;
}

