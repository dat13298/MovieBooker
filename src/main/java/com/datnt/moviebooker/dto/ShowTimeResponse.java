package com.datnt.moviebooker.dto;

import java.sql.Timestamp;

public record ShowTimeResponse(
        Long id,
        MovieResponse movie,
        Long screenId,
        Timestamp startTime,
        String presentation
) {}
