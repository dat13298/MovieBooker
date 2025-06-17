package com.datnt.moviebooker.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TheaterRequest {

    @NotNull(message = "Name cannot be null")
    @Size(min = 5, max = 50, message = "Name must be between 5 and 50 characters")
    private String name;

    @NotNull(message = "Address cannot be null")
    @Size(min = 5, max = 50, message = "Address must be between 5 and 50 characters")
    private String address;

    @NotNull
    private Long regionId;
}
