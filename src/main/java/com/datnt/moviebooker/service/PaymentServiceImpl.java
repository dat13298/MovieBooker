package com.datnt.moviebooker.service;

import com.datnt.moviebooker.config.VnPayConfig;
import com.datnt.moviebooker.constant.PaymentStatus;
import com.datnt.moviebooker.constant.Status;
import com.datnt.moviebooker.dto.PaymentRequest;
import com.datnt.moviebooker.dto.PaymentResponse;
import com.datnt.moviebooker.entity.Booking;
import com.datnt.moviebooker.entity.EvoucherTransaction;
import com.datnt.moviebooker.entity.Payment;
import com.datnt.moviebooker.repository.BookingRepository;
import com.datnt.moviebooker.repository.PaymentRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentServiceImpl.class);
    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final VnPayConfig vnPayConfig;
    private final WebSocketService webSocketService;
    private final PointService pointService;
    private final EvoucherTransactionService evoucherTransactionService;

    @Override
    public PaymentResponse createPayment(PaymentRequest request) {
        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        String vnpTxnRef = UUID.randomUUID().toString().replace("-", "").substring(0, 20);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expire = now.plusMinutes(15);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

        Payment payment = Payment.builder()
                .booking(booking)
                .vnpTxnRef(vnpTxnRef)
                .status(PaymentStatus.PENDING)
                .amount(booking.getTotalAmount())
                .build();
        paymentRepository.save(payment);

        Map<String, String> vnpParams = new TreeMap<>();
        vnpParams.put("vnp_Version", "2.1.0");
        vnpParams.put("vnp_Command", "pay");
        vnpParams.put("vnp_TmnCode", vnPayConfig.getTmnCode());
        vnpParams.put("vnp_Amount", String.valueOf(booking.getTotalAmount() * 100));
        vnpParams.put("vnp_CurrCode", "VND");
        vnpParams.put("vnp_TxnRef", vnpTxnRef);
        vnpParams.put("vnp_OrderInfo", "Thanh toan ve #" + booking.getId());
        vnpParams.put("vnp_OrderType", "other");
        vnpParams.put("vnp_ReturnUrl", vnPayConfig.getReturnUrl());
        vnpParams.put("vnp_IpAddr", "127.0.0.1");
        vnpParams.put("vnp_Locale", "vn");
        vnpParams.put("vnp_CreateDate", now.format(formatter));
        vnpParams.put("vnp_ExpireDate", expire.format(formatter));

        // Build hashData & queryUrl
        StringBuilder hashData = new StringBuilder();
        StringBuilder queryUrl = new StringBuilder();

        for (Map.Entry<String, String> entry : vnpParams.entrySet()) {
            String encodedValue = URLEncoder.encode(entry.getValue(), StandardCharsets.US_ASCII); // ✅ encode value cho hashData

            // HashData dùng key=value (value đã encode)
            hashData.append(entry.getKey()).append('=').append(encodedValue).append('&');

            // queryUrl cũng dùng encode tương tự
            queryUrl.append(URLEncoder.encode(entry.getKey(), StandardCharsets.US_ASCII))
                    .append('=')
                    .append(encodedValue)
                    .append('&');
        }


        hashData.setLength(hashData.length() - 1);
        queryUrl.setLength(queryUrl.length() - 1);

        String secureHash = hmacSHA512(vnPayConfig.getHashSecret(), hashData.toString());
        queryUrl.append("&vnp_SecureHash=").append(secureHash);
        System.out.println(hashData.toString());
        return PaymentResponse.builder()
                .paymentUrl(vnPayConfig.getPayUrl() + "?" + queryUrl)
                .build();
    }


    @Override
    public String handleVnpayReturn(HttpServletRequest request) {
        String queryString = request.getQueryString();
        if (queryString == null) return "No query provided";

        Map<String, String> inputData = Arrays.stream(queryString.split("&"))
                .map(p -> p.split("=", 2))
                .filter(arr -> !arr[0].equals("vnp_SecureHash") && !arr[0].equals("vnp_SecureHashType"))
                .collect(Collectors.toMap(
                        arr -> arr[0],
                        arr -> arr.length > 1 ? arr[1] : "",
                        (a, b) -> a,
                        TreeMap::new
                ));

        StringBuilder hashData = new StringBuilder();
        for (Map.Entry<String, String> entry : inputData.entrySet()) {
            hashData.append(entry.getKey()).append('=').append(entry.getValue()).append('&');
        }
        hashData.setLength(hashData.length() - 1);

        String secureHash = hmacSHA512(vnPayConfig.getHashSecret(), hashData.toString());
        String receivedHash = request.getParameter("vnp_SecureHash");

        if (!secureHash.equalsIgnoreCase(receivedHash)) {
            System.out.println("Hash mismatch:\nComputed: " + secureHash + "\nReceived: " + receivedHash + "\nData: " + hashData);
            return "Payment Failed! Invalid signature. The data is not trustworthy.";
        }

        String vnpTxnRef = request.getParameter("vnp_TxnRef");
        String vnpResponseCode = request.getParameter("vnp_ResponseCode");
        String vnpTransactionNo = request.getParameter("vnp_TransactionNo");
        String bankCode = request.getParameter("vnp_BankCode");

        Payment payment = paymentRepository.findByVnpTxnRef(vnpTxnRef)
                .orElseThrow(() -> new RuntimeException("Invalid vnp_TxnRef"));

        if ("00".equals(vnpResponseCode)) {
            payment.setStatus(PaymentStatus.SUCCESS);
            payment.getBooking().setStatus(Status.SUCCESS);
        } else {
            payment.setStatus(PaymentStatus.FAILED);
        }

        payment.setPayDate(LocalDateTime.now());
        payment.setVnpResponseCode(vnpResponseCode);
        payment.setVnpTransactionNo(vnpTransactionNo);
        payment.setBankCode(bankCode);
        paymentRepository.save(payment);

        webSocketService.sendBookingStatus(payment.getBooking().getId(),
                payment.getStatus().name());
        // Tích điểm cho người dùng để đổi voucher
        if (payment.getStatus() == PaymentStatus.SUCCESS) {
            var point = pointService.findByUserId(payment.getBooking().getUser().getId());
            if (point != null) {
                if (payment.getAmount() > 85000) {
                    int pointsToAdd = (int) Math.ceil(payment.getAmount() * 0.08); // 8% of the payment amount, rounded up
                    point.setAvailablePoints(pointsToAdd + point.getAvailablePoints());
                    point.setTotalPoints(pointsToAdd + point.getTotalPoints());
                    pointService.savePoint(point);

                    EvoucherTransaction evoucherTransaction = EvoucherTransaction.builder()
                            .user(payment.getBooking().getUser())
                            .evoucher(null)
                            .description("Tích điểm khi thanh toán thành công với đơn hàng có giá trị " + payment.getAmount() + " VND")
                            .status("SUCCESS")
                            .transactionDate(LocalDateTime.now())
                            .transactionType(EvoucherTransaction.TransactionType.EARN)
                            .points(pointsToAdd)
                            .pointsBefore(point.getAvailablePoints() - pointsToAdd)
                            .pointsAfter(point.getAvailablePoints())
                            .build();
                    evoucherTransactionService.createEvoucherTransaction(evoucherTransaction);

                    log.info("Earned {} points for user ID: {}", pointsToAdd, payment.getBooking().getUser().getId());
                } else {
                    log.info("Payment amount is too low to earn points: {}", payment.getAmount());
                }
            }
        }

        return "Payment " + payment.getStatus();
    }

    private String hmacSHA512(String key, String data) {
        try {
            javax.crypto.Mac hmac = javax.crypto.Mac.getInstance("HmacSHA512");
            javax.crypto.spec.SecretKeySpec secretKey = new javax.crypto.spec.SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            hmac.init(secretKey);
            byte[] bytes = hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(bytes);
        } catch (Exception ex) {
            throw new RuntimeException("Unable to sign data", ex);
        }
    }

    private String bytesToHex(byte[] hash) {
        StringBuilder result = new StringBuilder();
        for (byte b : hash) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }
}
