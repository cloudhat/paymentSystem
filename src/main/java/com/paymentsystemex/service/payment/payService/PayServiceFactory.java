package com.paymentsystemex.service.payment.payService;

import com.paymentsystemex.domain.payment.PaymentMethod;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class PayServiceFactory {
    private final HashMap<PaymentMethod, PayService> serviceMap = new HashMap<>();

    public PayServiceFactory(List<PayService> payServiceList) {
        for (PayService payService : payServiceList) {
            if (!serviceMap.containsKey(payService.getPaymentMethod())) {
                serviceMap.put(payService.getPaymentMethod(), payService);
            } else {
                throw new IllegalArgumentException("Duplicated PayService is not allowed");
            }
        }
    }

    public PayService getPayService(PaymentMethod paymentMethod){
        return serviceMap.get(paymentMethod);
    }
}
