package com.datnt.moviebooker.controller;

import com.datnt.moviebooker.common.ApiWrapperResponse;
import com.datnt.moviebooker.dto.GetHistoryEvoucherTransactionResponse;
import com.datnt.moviebooker.service.AuthService;
import com.datnt.moviebooker.service.EvoucherTransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * Controller class for managing E-voucher transactions
 * Provides endpoints for retrieving transaction history and managing e-voucher related operations
 */
@RestController
@RequestMapping("/api/v1/evoucher-transactions")
@Tag(name = "E-voucher Transaction Controller", description = "APIs for managing e-voucher transactions")
@RequiredArgsConstructor
public class EvoucherTransactionController {

    private final EvoucherTransactionService evoucherTransactionService;
    private final AuthService authService;

    /**
     * Retrieve transaction history for a specific user within an optional date range
     *
     * @param startDate Optional start date to filter transactions (format: yyyy-MM-dd)
     * @param endDate   Optional end date to filter transactions (format: yyyy-MM-dd)
     * @return ApiWrapperResponse containing list of transaction history responses
     */
    @Operation(summary = "Get user's e-voucher transaction history",
            description = "Retrieves the e-voucher transaction history for a specific user. " +
                    "If no date range is provided, returns the last 7 days of transactions. " +
                    "Time range will be from 00:00:00 of start date to 23:59:59 of end date.")
    @GetMapping("/history")
    public ApiWrapperResponse<List<GetHistoryEvoucherTransactionResponse>> getTransactionHistory(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime endDateTime = endDate != null ? endDate.atTime(LocalTime.MAX) : null;

        return evoucherTransactionService.getTransactionHistory(
                authService.getCurrentUserId(),
                startDateTime,
                endDateTime
        );
    }
}
