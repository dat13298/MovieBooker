package com.datnt.moviebooker.service;

import com.datnt.moviebooker.common.ApiWrapperResponse;
import com.datnt.moviebooker.common.ResponseCode;
import com.datnt.moviebooker.dto.GetAllVoucherResponse;
import com.datnt.moviebooker.dto.GetVoucherUseResponse;
import com.datnt.moviebooker.entity.Evoucher;
import com.datnt.moviebooker.exception.BusinessException;
import com.datnt.moviebooker.repository.EvoucherRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class EvoucherService {

    private final EvoucherRepository evoucherRepository;
    private final ObjectMapper objectMapper;
    private final AuthService authService;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public ApiWrapperResponse<List<GetAllVoucherResponse>> getUnusedVouchers() {
        List<Evoucher> unusedEvouchers = evoucherRepository.findByCreatedByAndStatus(
                String.valueOf(authService.getCurrentUserId()), String.valueOf(Evoucher.Status.UNUSED));
        if (unusedEvouchers.isEmpty()) {
            throw new BusinessException(ResponseCode.EVOUCHER_NOT_FOUND);
        }
        List<GetAllVoucherResponse> response = unusedEvouchers.stream()
                .map(evoucher -> objectMapper.convertValue(evoucher, GetAllVoucherResponse.class))
                .collect(Collectors.toList());
        return ApiWrapperResponse.success(response);
    }

    public ApiWrapperResponse<List<GetAllVoucherResponse>> getOtherVouchers() {
        List<Evoucher> otherEvouchers = evoucherRepository.findByCreatedByAndStatusNot(
                String.valueOf(authService.getCurrentUserId()), String.valueOf(Evoucher.Status.UNUSED));
        if (otherEvouchers.isEmpty()) {
            throw new BusinessException(ResponseCode.EVOUCHER_NOT_FOUND);
        }
        List<GetAllVoucherResponse> response = otherEvouchers.stream()
                .map(evoucher -> objectMapper.convertValue(evoucher, GetAllVoucherResponse.class))
                .collect(Collectors.toList());
        return ApiWrapperResponse.success(response);
    }

    public ApiWrapperResponse<GetVoucherUseResponse> useVoucher(Long evoucherId) {
        Evoucher evoucher = evoucherRepository.findById(evoucherId)
                .orElseThrow(() -> new BusinessException(ResponseCode.EVOUCHER_NOT_FOUND));

        if (evoucher.getStatus() == Evoucher.Status.USED) {
            throw new BusinessException(ResponseCode.EVOUCHER_ALREADY_USED);
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiryDateTime = LocalDateTime.parse(evoucher.getExpiryDate(), DATE_FORMATTER)
                .plusDays(1)
                .minusSeconds(1);

        if (now.isAfter(expiryDateTime)) {
            evoucher.setStatus(Evoucher.Status.EXPIRED);
            evoucherRepository.save(evoucher);
            throw new BusinessException(ResponseCode.EVOUCHER_EXPIRED);
        }

        evoucher.setStatus(Evoucher.Status.USED);
        evoucherRepository.save(evoucher);

        GetVoucherUseResponse response = objectMapper.convertValue(evoucher, GetVoucherUseResponse.class);
        return ApiWrapperResponse.success(response);
    }
}
