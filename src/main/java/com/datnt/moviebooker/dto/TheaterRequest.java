package com.datnt.moviebooker.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TheaterRequest {

    @NotNull(message = "Name cannot be null")
    @Size(min = 5, max = 50, message = "Name must be between 5 and 50 characters")
    private String name;

    @NotNull(message = "Location cannot be null")
    @Size(min = 5, max = 50, message = "Location must be between 5 and 50 characters")
    private String location;
}
