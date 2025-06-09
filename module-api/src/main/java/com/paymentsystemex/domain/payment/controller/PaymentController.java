package com.paymentsystemex.domain.payment.controller;

import core.domain.member.entity.Member;
import core.domain.member.repository.MemberRepository;
import com.paymentsystemex.domain.payment.dto.PaymentInitResponse;
import core.domain.payment.dto.PaymentRequest;
import com.paymentsystemex.domain.payment.dto.PaymentTransactionResponse;
import core.domain.payment.entity.Payment;
import core.domain.payment.entity.PaymentStatus;
import core.domain.payment.repository.PaymentRepository;
import com.paymentsystemex.domain.payment.service.PaymentService;
import com.paymentsystemex.global.exception.AuthenticationException;
import com.paymentsystemex.global.auth.principal.AuthenticationPrincipal;
import com.paymentsystemex.global.auth.principal.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    private final MemberRepository memberRepository;

    private final PaymentRepository paymentRepository;


    @PostMapping("/init")
    public ResponseEntity<PaymentInitResponse> initPayment(@AuthenticationPrincipal UserPrincipal userPrincipal, @RequestBody PaymentRequest paymentRequest) {
        return ResponseEntity.ok(paymentService.initPayment(paymentRequest, userPrincipal));
    }

    @PostMapping("/transaction")
    public ResponseEntity<Object> executeTransaction(@AuthenticationPrincipal UserPrincipal userPrincipal, @RequestParam Long paymentId, @RequestParam String payKey) {
        Member member = memberRepository.findByEmail(userPrincipal.getUsername()).orElseThrow(AuthenticationException::new);

        //1. 실제 거래를 위한 초기화
        Payment payment = paymentService.initTransaction(paymentId, member.getId(), payKey);

        //2.외부 결제 서버에 결제 요청
        try {
            paymentService.requestTransaction(payment.getPaymentMethod(), paymentId, payKey, payment.getTotalPayAmount());
        } catch (Exception e) {
            paymentRepository.updatePaymentStatus(paymentId, PaymentStatus.FAIL);
            PaymentTransactionResponse paymentTransactionResponse = new PaymentTransactionResponse(e.getMessage(), payment.getExpireTime(), PaymentStatus.FAIL);
            return ResponseEntity.internalServerError().body(paymentTransactionResponse);
        }

        //3.주문 및 결제 entity의 상태를 완료로 변경
        paymentService.completeTransaction(paymentId , member.getId());

        return ResponseEntity.status(HttpStatus.OK).location(URI.create("/orders/history")).build();
    }

}
