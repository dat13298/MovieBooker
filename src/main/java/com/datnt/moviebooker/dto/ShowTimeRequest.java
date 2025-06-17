package com.datnt.moviebooker.dto;

import jakarta.validation.constraints.NotNull;
import java.sql.Timestamp;

public record ShowTimeRequest(
        @NotNull(message = "Movie ID cannot be null") Long movieId,
        @NotNull(message = "Screen ID cannot be null") Long screenId,
        @NotNull(message = "Start Time cannot be null") Timestamp startTime,
        @NotNull String presentation
) {}
