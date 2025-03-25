package com.datnt.moviebooker.dto;

import java.math.BigDecimal;
import java.sql.Timestamp;

public record ShowTimeResponse(
        Long id,
        Long movieId,
        Long screenId,
        Timestamp startTime,
        BigDecimal price
) {}
