package com.datnt.moviebooker.controller;

import com.datnt.moviebooker.dto.PaymentRequest;
import com.datnt.moviebooker.dto.PaymentResponse;
import com.datnt.moviebooker.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/create")
    public PaymentResponse createPayment(@RequestBody PaymentRequest request) {
        return paymentService.createPayment(request);
    }

    @GetMapping("/vnpay-return")
    public String handleVnpayReturn(HttpServletRequest request) {
        return paymentService.handleVnpayReturn(request);
    }
}
