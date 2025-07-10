package com.datnt.moviebooker.dto;

import com.datnt.moviebooker.constant.SeatStatus;
import com.datnt.moviebooker.constant.SeatType;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SeatRequestWithId {
    private Long id;

    @NotNull @Size(min = 2, max = 5)
    private String seatNumber;

    @NotNull
    private Long screenId;

    @NotNull
    private Long showTimeId;

    @NotNull
    private Long price;

    @NotNull
    private SeatType seatType;

    @NotNull
    private SeatStatus status;

    @NotNull
    private Integer rowIdx;

    @NotNull
    private Integer colIdx;
}
