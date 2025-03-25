package com.datnt.moviebooker.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.sql.Timestamp;

public record ShowTimeRequest(
        @NotNull(message = "Movie ID cannot be null") Long movieId,
        @NotNull(message = "Screen ID cannot be null") Long screenId,
        @NotNull(message = "Start Time cannot be null") Timestamp startTime,
        @NotNull(message = "Price cannot be null") @Min(value = 0, message = "Price must be >= 0") BigDecimal price
) {}
