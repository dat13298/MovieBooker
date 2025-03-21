package com.datnt.moviebooker.dto;

import lombok.Data;
import java.util.List;

@Data
public class BookingRequest {
    private Long showTimeId;
    private List<Long> seatIds;
}
