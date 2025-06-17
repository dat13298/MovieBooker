package com.datnt.moviebooker.service;

import com.datnt.moviebooker.dto.PaymentRequest;
import com.datnt.moviebooker.dto.PaymentResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface PaymentService {
    PaymentResponse createPayment(PaymentRequest request);
    String handleVnpayReturn(HttpServletRequest request);
}
