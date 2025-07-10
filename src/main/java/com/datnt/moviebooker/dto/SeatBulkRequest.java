package com.datnt.moviebooker.dto;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.*;
import java.util.List;

@Getter @Setter
public class SeatBulkRequest {

    @NotEmpty
    private List<SeatRequestWithId> seats;
}
