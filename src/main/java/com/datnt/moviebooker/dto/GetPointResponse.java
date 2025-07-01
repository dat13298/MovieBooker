package com.datnt.moviebooker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetPointResponse {
    private int availablePoints;
    private int redeemedPoints;
    private int totalPoints;
}
