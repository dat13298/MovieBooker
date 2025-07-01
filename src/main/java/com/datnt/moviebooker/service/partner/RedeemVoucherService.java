package com.datnt.moviebooker.service.partner;

import com.datnt.moviebooker.common.ApiWrapperResponse;
import com.datnt.moviebooker.common.ResponseCode;
import com.datnt.moviebooker.dto.partner.request.RedeemVoucherRequest;
import com.datnt.moviebooker.entity.Point;
import com.datnt.moviebooker.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class RedeemVoucherService {
    private final RedeemVoucherServiceImpl redeemVoucherServiceImpl;
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 1000; // 1 giây thời gian delay khi retry lại

    @Transactional
    public ApiWrapperResponse<String> handlerRedeemVoucher(RedeemVoucherRequest request) {
        try {
            // Step 1: Kiểm tra điểm của người dùng
            Long userId = redeemVoucherServiceImpl.checkPoint(request);

            // Step 2: Trừ điểm của người dùng
            Point updatedPoint = redeemVoucherServiceImpl.deductPoint(userId, request);

            // Step 3: Gọi API GotIt để đổi voucher với retry logic
            int retryCount = 0;
            Exception lastException = null;

            while (retryCount < MAX_RETRIES) {
                try {
                    String result = redeemVoucherServiceImpl.redeemVoucher(request, updatedPoint);
                    return ApiWrapperResponse.success(result);
                } catch (Exception e) {
                    lastException = e;
                    retryCount++;
                    log.warn("Attempt {} failed to redeem voucher. Error: {}", retryCount, e.getMessage());

                    if (retryCount < MAX_RETRIES) {
                        try {
                            Thread.sleep(RETRY_DELAY_MS);
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            break;
                        }
                    }
                }
            }

            // Nếu tất cả các lần retry đều thất bại
            log.error("All {} retry attempts failed", MAX_RETRIES);
            redeemVoucherServiceImpl.rollBackPoint(userId, request);
            throw lastException;

        } catch (BusinessException e) {
            log.error("Error during voucher redemption process: {}", e.getMessage());
            return ApiWrapperResponse.error(e.getResponseCode(), e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error during voucher redemption: {}", e.getMessage(), e);
            return ApiWrapperResponse.error(ResponseCode.REDEEM_VOUCHER_FAILED);
        }
    }
}
