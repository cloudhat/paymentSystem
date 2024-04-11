package com.paymentsystemex.service.payment.payService;

import com.paymentsystemex.domain.payment.PaymentMethod;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class TossPayService implements PayService {

    private final String secretKey  = "tossSecretKeyExample";

    @Override
    public PaymentMethod getPaymentMethod() {
        return PaymentMethod.TOSS_PAY;
    }


    @Override
    public void requestTransaction() {

    }
}
