package com.datnt.moviebooker.service.partner;

import com.datnt.moviebooker.common.GotitClient;
import com.datnt.moviebooker.common.ResponseCode;
import com.datnt.moviebooker.dto.partner.request.RedeemVoucherRequest;
import com.datnt.moviebooker.dto.partner.response.RedeemVoucherResponse;
import com.datnt.moviebooker.entity.Evoucher;
import com.datnt.moviebooker.entity.EvoucherTransaction;
import com.datnt.moviebooker.entity.Point;
import com.datnt.moviebooker.entity.User;
import com.datnt.moviebooker.exception.BusinessException;
import com.datnt.moviebooker.repository.EvoucherRepository;
import com.datnt.moviebooker.repository.EvoucherTransactionRepository;
import com.datnt.moviebooker.repository.PointRepository;
import com.datnt.moviebooker.repository.UserRepository;
import com.datnt.moviebooker.security.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.datnt.moviebooker.common.GotitClient.PREFIX_REDEEM_VOUCHER;

@Service
@Slf4j
@RequiredArgsConstructor
public class RedeemVoucherServiceImpl {
    private final PointRepository pointRepository;
    private final EvoucherRepository evoucherRepository;
    private final EvoucherTransactionRepository evoucherTransactionRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final GotitClient gotitClient;
    private final ObjectMapper objectMapper;

    // Step 1: Kiểm tra điểm của người dùng
    public Long checkPoint(RedeemVoucherRequest request) {
        // Kiểm tra xem người dùng có tồn tại không
        var user = userRepository.findByUsername(jwtService.getUsernameFromCurrentRequestToken())
                .orElseThrow(() -> new BusinessException(ResponseCode.USER_NOT_FOUND));

        // Lấy điểm của người dùng
        var point = pointRepository.findByUserId(user.getId())
                .orElseThrow(() -> new BusinessException(ResponseCode.NOT_FOUND));

        // So sánh số điểm hiện tại của user với giá trị của voucher
        if (point.getAvailablePoints() < request.getPriceValue()) {
            throw new BusinessException(ResponseCode.POINT_NOT_ENOUGH);
        }
        return user.getId();
    }

    // Step 2: Trừ điểm của người dùng tương ứng với giá trị voucher
    public Point deductPoint(Long userId, RedeemVoucherRequest request) {
        // Lấy điểm của người dùng
        var point = pointRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(ResponseCode.NOT_FOUND));

        // Trừ điểm của người dùng
        point.setAvailablePoints(point.getAvailablePoints() - request.getPriceValue());
        point.setRedeemedPoints(request.getPriceValue());
        pointRepository.save(point);
        log.info("User ID: {}, Redeemed Points: {}, Remaining Points: {}",
                 userId, request.getPriceValue(), point.getAvailablePoints());

        return point;
    }

    // Step 3: Gọi API GotIt để đổi voucher
    public String redeemVoucher(RedeemVoucherRequest request, Point point) {
        // Lấy thông tin user
        var user = userRepository.findByUsername(jwtService.getUsernameFromCurrentRequestToken())
                .orElseThrow(() -> new BusinessException(ResponseCode.USER_NOT_FOUND));

        // Tạo data gửi sang GotIt
        Map<String, Object> data = new HashMap<>();
        data.put("productId", request.getGiftId());
        data.put("productPriceId", request.getPriceId());
        data.put("quantity", 1);
        data.put("expiryDate", LocalDate.now().plusDays(90).toString());
        data.put("orderName", "Đổi quà MovieBooker");
        data.put("transactionRefId", PREFIX_REDEEM_VOUCHER + UUID.randomUUID());

        try {
            // Convert data to JSON and call API
            String requestBody = objectMapper.writeValueAsString(data);
            HttpResponse<String> response = gotitClient.callApi(
                    "POST",
                    GotitClient.PATH_REDEEM_VOUCHER,
                    null,
                    requestBody
            );

            if (response.statusCode() == 200) {
                RedeemVoucherResponse redeemResponse = objectMapper.readValue(
                        response.body(),
                        RedeemVoucherResponse.class
                );

                // Kiểm tra status từ API
                Evoucher evoucher = addEvoucher(request, redeemResponse, user);
                evoucherRepository.save(evoucher);

                // Tạo transaction
                EvoucherTransaction evoucherTransaction = new EvoucherTransaction();
                evoucherTransaction.setId(evoucher.getId());
                evoucherTransaction.setDescription("Đổi Evoucher: " + evoucher.getEvoucherName());
                evoucherTransaction.setStatus("SUCCESS");
                evoucherTransaction.setTransactionDate(LocalDateTime.now());
                evoucherTransaction.setUser(user);
                evoucherTransaction.setEvoucher(evoucher);
                evoucherTransaction.setTransactionType(EvoucherTransaction.TransactionType.REDEEM);
                evoucherTransaction.setPoints(request.getPriceValue());
                evoucherTransaction.setPointsAfter(point.getAvailablePoints());
                evoucherTransaction.setPointsBefore(point.getAvailablePoints() + request.getPriceValue());
                evoucherTransactionRepository.save(evoucherTransaction);

                log.info("Voucher redeemed successfully: {}, user: {}", evoucher.getCode(), user.getUsername());
                return "Đổi quà thành công, vui lòng xem trong kho voucher của bạn";
            } else {
                throw new BusinessException(ResponseCode.INTERNAL_SERVER_ERROR,
                        "GotIt API error: " + response.body());
            }
        } catch (IOException | InterruptedException e) {
            log.error("Error during voucher redemption: {}", e.getMessage(), e);
            throw new BusinessException(ResponseCode.INTERNAL_SERVER_ERROR,
                    "Error processing voucher redemption: " + e.getMessage());
        }
    }

    private static Evoucher addEvoucher(RedeemVoucherRequest request, RedeemVoucherResponse redeemResponse, User user) {
        if (!"OK".equals(redeemResponse.getStatus())) {
            throw new BusinessException(ResponseCode.INTERNAL_SERVER_ERROR,
                    "GotIt API error: " + redeemResponse.getError());
        }

        // Kiểm tra danh sách data
        if (redeemResponse.getData() == null || redeemResponse.getData().isEmpty()) {
            throw new BusinessException(ResponseCode.INTERNAL_SERVER_ERROR,
                    "GotIt API error: Data list is null or empty");
        }

        // Lấy data item đầu tiên
        RedeemVoucherResponse.DataItem dataItem = redeemResponse.getData().get(0);

        // Kiểm tra danh sách vouchers
        if (dataItem.getVouchers() == null || dataItem.getVouchers().isEmpty()) {
            throw new BusinessException(ResponseCode.INTERNAL_SERVER_ERROR,
                    "GotIt API error: Vouchers list is null or empty");
        }

        return addEvoucher(request, user, dataItem);
    }

    private static Evoucher addEvoucher(RedeemVoucherRequest request, User user, RedeemVoucherResponse.DataItem dataItem) {
        RedeemVoucherResponse.Voucher firstVoucher = dataItem.getVouchers().get(0);

        // Tạo Evoucher entity
        Evoucher evoucher = new Evoucher();
        evoucher.setEvoucherName(firstVoucher.getProduct().getProductNm());
        evoucher.setCode(firstVoucher.getVoucherCode());
        evoucher.setBrandName(firstVoucher.getProduct().getBrandNm());
        evoucher.setPointsRequired(request.getPriceValue());
        evoucher.setExpiryDate(firstVoucher.getExpiryDate());
        evoucher.setGiftId(String.valueOf(request.getGiftId()));
        evoucher.setStatus(Evoucher.Status.UNUSED);
        evoucher.setSerial(firstVoucher.getVoucherSerial());
        evoucher.setTypeCode(Evoucher.TypeCode.TEXTCODE);
        evoucher.setImgUrl(firstVoucher.getVoucherImageLink());
        evoucher.setRefId(firstVoucher.getTransactionRefId());
        evoucher.setCreatedBy(user.getId().toString());
        return evoucher;
    }

    // Step Exception: Hoàn lại điểm nếu có lỗi xảy ra từ bước 2
    public void rollBackPoint(Long userId, RedeemVoucherRequest request) {
        // Lấy điểm của người dùng
        var point = pointRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(ResponseCode.NOT_FOUND));

        // Hoàn lại điểm cho người dùng
        point.setAvailablePoints(point.getAvailablePoints() + request.getPriceValue());
        point.setRedeemedPoints(point.getRedeemedPoints() - request.getPriceValue());
        pointRepository.save(point);
        log.info("Rolled back points for user ID: {}, Points returned: {}, Total points: {}",
                 userId, request.getPriceValue(), point.getAvailablePoints());

    }
}
