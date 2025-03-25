package com.datnt.moviebooker.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SeatRequest {
    @NotNull(message = "Seat Number cannot be null")
    @Size(min = 2, max = 5, message = "Seat Number must be between 2 and 5 characters")
    private String seatNumber;

    @NotNull(message = "Screen ID cannot be null")
    private Long screenId;
}
