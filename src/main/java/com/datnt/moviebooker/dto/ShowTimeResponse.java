package com.datnt.moviebooker.dto;

import java.sql.Timestamp;

public record ShowTimeResponse(
        Long id,
        MovieResponse movie,
        ScreenResponse screen,
        Timestamp startTime,
        Timestamp endTime,
        String presentation,
        int seatCount
) {}
