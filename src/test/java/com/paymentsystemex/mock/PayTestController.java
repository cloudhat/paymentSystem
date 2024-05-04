package com.paymentsystemex.mock;

import com.paymentsystemex.domain.payment.Payment;
import com.paymentsystemex.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequiredArgsConstructor
public class PayTestController {

    public final static String PAY_KEY = "payKeyExample";
    public final static String INVALID_PAY_KEY = "invalidPaymentKey";
    public final static String FAULURE_MSG = "잘못된 요청입니다.";

    private final OrderRepository orderRepository;

    @PostMapping("/v1/payments/confirm")
    public ResponseEntity<JSONObject> tossConfirm(@RequestBody JSONObject jsonObject) {
        String payKey = (String) jsonObject.get("paymentKey");

        JSONObject result = new JSONObject();

        if (payKey.equals(PAY_KEY)) {
            return ResponseEntity.ok(result);
        }

        JSONObject failure = new JSONObject();
        failure.put("message", FAULURE_MSG);
        result.put("failure", failure);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);

    }

    @PostMapping("/v1/payments/{paymentId}/cancel")
    public ResponseEntity<JSONObject> tossCancel(@PathVariable String paymentId) {
        String payKey = paymentId;

        JSONObject result = new JSONObject();

        if (payKey.equals(PAY_KEY)) {
            return ResponseEntity.ok(result);
        }

        JSONObject failure = new JSONObject();
        failure.put("message", FAULURE_MSG);
        result.put("failure", failure);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);

    }

    @PostMapping("/naverpay-partner/naverpay/payments/v2.2/apply/payment")
    public ResponseEntity<JSONObject> naverPayments(@RequestParam String paymentId) {
        String payKey = paymentId ;

        JSONObject result = new JSONObject();
        result.put("code", "Success");


        if (payKey.equals(PAY_KEY)) {
            Payment payment = orderRepository.findPaymentByPaykey(payKey).get();
            result.put("code", "Success");

            JSONObject detail = new JSONObject();
            detail.put("totalPayAmount", payment.getTotalPayAmount());
            result.put("detail", detail);
        } else if (payKey.equals(INVALID_PAY_KEY)) {
            result.put("code", "Success");

            JSONObject detail = new JSONObject();
            detail.put("totalPayAmount", 1000); //엉뚱한 결제금액 return
            result.put("detail", detail);
        } else {
            result.put("code", "Fail");
            result.put("message", FAULURE_MSG);
        }

        return ResponseEntity.ok(result);
    }

    @PostMapping("/naverpay-partner/naverpay/payments/v1/cancel")
    public ResponseEntity<JSONObject> naverCancel(@RequestParam String paymentId) {
        String payKey = paymentId ;

        JSONObject result = new JSONObject();
        result.put("code", "Success");


        if (payKey.equals(PAY_KEY)) {
            Payment payment = orderRepository.findPaymentByPaykey(payKey).get();
            result.put("code", "Success");

            JSONObject detail = new JSONObject();
            detail.put("totalPayAmount", payment.getTotalPayAmount());
            result.put("detail", detail);
        } else if (payKey.equals(INVALID_PAY_KEY)) {
            result.put("code", "Success");

            JSONObject detail = new JSONObject();
            detail.put("totalPayAmount", 1000); //엉뚱한 결제금액 return
            result.put("detail", detail);
        } else {
            result.put("code", "Fail");
            result.put("message", FAULURE_MSG);
        }

        return ResponseEntity.ok(result);
    }
}

