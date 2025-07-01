package com.datnt.moviebooker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetHistoryEvoucherTransactionResponse {
    private String description;
    private String status;
    private LocalDateTime transactionDate;
    private int pointsUsed;
    private int pointsAfter;
    private int pointsBefore;
}
