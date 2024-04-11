package com.paymentsystemex.controller;

import com.paymentsystemex.auth.principal.AuthenticationPrincipal;
import com.paymentsystemex.auth.principal.UserPrincipal;
import com.paymentsystemex.dto.payment.PaymentRequest;
import com.paymentsystemex.dto.payment.PaymentResponse;
import com.paymentsystemex.service.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("init")
    public ResponseEntity<PaymentResponse> initPayment(@AuthenticationPrincipal UserPrincipal userPrincipal, @RequestBody PaymentRequest paymentRequest) {
        return ResponseEntity.ok(paymentService.initPayment(paymentRequest, userPrincipal));
    }

}
