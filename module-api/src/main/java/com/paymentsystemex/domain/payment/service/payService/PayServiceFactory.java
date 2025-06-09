package com.paymentsystemex.domain.payment.service.payService;

import core.domain.order.entity.payment.PaymentMethod;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
public class PayServiceFactory {
    private final Map<PaymentMethod, PayService> serviceMap = new EnumMap<>(PaymentMethod.class);

    public PayServiceFactory(List<PayService> payServiceList) {
        for (PayService payService : payServiceList) {
            if (!serviceMap.containsKey(payService.getPaymentMethod())) {
                serviceMap.put(payService.getPaymentMethod(), payService);
            } else {
                throw new IllegalArgumentException("Duplicated PayService is not allowed");
            }
        }
    }

    public PayService getPayService(PaymentMethod paymentMethod) {
        return serviceMap.get(paymentMethod);
    }
}
