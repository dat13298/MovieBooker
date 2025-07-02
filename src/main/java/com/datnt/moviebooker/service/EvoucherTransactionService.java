package com.datnt.moviebooker.service;

import com.datnt.moviebooker.common.ApiWrapperResponse;
import com.datnt.moviebooker.common.ResponseCode;
import com.datnt.moviebooker.dto.GetHistoryEvoucherTransactionResponse;
import com.datnt.moviebooker.entity.EvoucherTransaction;
import com.datnt.moviebooker.exception.BusinessException;
import com.datnt.moviebooker.repository.EvoucherTransactionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class EvoucherTransactionService {
    private final EvoucherTransactionRepository evoucherTransactionRepository;

    public ApiWrapperResponse<List<GetHistoryEvoucherTransactionResponse>> getTransactionHistory(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        // If no dates provided, get last 7 days
        LocalDateTime defaultStartDate = LocalDateTime.now().minusDays(7);

        List<EvoucherTransaction> transactions = evoucherTransactionRepository.findTransactionHistory(userId, startDate, endDate, defaultStartDate);
        if (transactions.isEmpty()) {
            throw new BusinessException(ResponseCode.EVOUCHER_TRANSACTION_NOT_FOUND);
        }

        return ApiWrapperResponse.success(ResponseCode.SUCCESS,transactions.stream()
                .map(transaction -> GetHistoryEvoucherTransactionResponse.builder()
                        .description(transaction.getDescription())
                        .status(transaction.getStatus())
                        .transactionDate(transaction.getTransactionDate())
                        .pointsUsed(transaction.getPoints())
                        .pointsAfter(transaction.getPointsAfter())
                        .pointsBefore(transaction.getPointsBefore())
                        .build())
                .collect(Collectors.toList()));
    }

    public EvoucherTransaction findByUserId(Long userId) {
        return evoucherTransactionRepository.findByUserId(userId);
    }

    public void saveEvoucherTransaction(EvoucherTransaction evoucherTransaction) {
        if (evoucherTransaction == null || evoucherTransaction.getUser() == null || evoucherTransaction.getUser().getId() == null) {
            throw new BusinessException(ResponseCode.EVOUCHER_TRANSACTION_FAILED);
        }
        evoucherTransactionRepository.save(evoucherTransaction);
    }

    public void createEvoucherTransaction(EvoucherTransaction evoucherTransaction) {
        if (evoucherTransaction == null || evoucherTransaction.getUser() == null || evoucherTransaction.getUser().getId() == null) {
            throw new BusinessException(ResponseCode.EVOUCHER_CAN_NOT_CREATE);
        }
        evoucherTransactionRepository.save(evoucherTransaction);
    }
}
