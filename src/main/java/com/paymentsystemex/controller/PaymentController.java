package com.paymentsystemex.controller;

import com.paymentsystemex.auth.AuthenticationException;
import com.paymentsystemex.auth.principal.AuthenticationPrincipal;
import com.paymentsystemex.auth.principal.UserPrincipal;
import com.paymentsystemex.domain.member.Member;
import com.paymentsystemex.domain.payment.Payment;
import com.paymentsystemex.domain.payment.PaymentStatus;
import com.paymentsystemex.dto.payment.PaymentInitResponse;
import com.paymentsystemex.dto.payment.PaymentRequest;
import com.paymentsystemex.dto.payment.PaymentTransactionResponse;
import com.paymentsystemex.repository.MemberRepository;
import com.paymentsystemex.repository.OrderRepository;
import com.paymentsystemex.service.payment.PaymentService;
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

    private final OrderRepository orderRepository;

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
            orderRepository.updatePaymentStatus(paymentId, PaymentStatus.FAIL);
            PaymentTransactionResponse paymentTransactionResponse = new PaymentTransactionResponse(e.getMessage(), payment.getExpireTime(), PaymentStatus.FAIL);
            return ResponseEntity.internalServerError().body(paymentTransactionResponse);
        }

        //3.주문 및 결제 entity의 상태를 완료로 변경
        paymentService.completeTransaction(paymentId , member.getId());

        return ResponseEntity.status(HttpStatus.OK).location(URI.create("/orders/history")).build();
    }

}
