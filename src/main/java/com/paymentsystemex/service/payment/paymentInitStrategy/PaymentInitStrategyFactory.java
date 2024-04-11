package com.paymentsystemex.service.payment.paymentInitStrategy;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class PaymentInitStrategyFactory {


    private final HashMap<String, PaymentInitStrategy> strategyMap = new HashMap<>();

    public PaymentInitStrategyFactory(List<PaymentInitStrategy> paymentInitStrategyList) {
        for (PaymentInitStrategy paymentInitStrategy : paymentInitStrategyList) {
            if (!strategyMap.containsKey(paymentInitStrategy.getEventType())) {
                strategyMap.put(paymentInitStrategy.getEventType(), paymentInitStrategy);
            } else {
                throw new IllegalArgumentException("Duplicated PaymentInitStrategy is not allowed");
            }
        }
    }

    public PaymentInitStrategy getInitStrategy(String eventType){
        return strategyMap.get(eventType);
    }
}
