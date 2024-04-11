package com.paymentsystemex.service.payment.payService;

import com.paymentsystemex.domain.payment.PaymentMethod;

import java.util.HashMap;

public interface PayService {

    PaymentMethod getPaymentMethod();


    void requestTransaction();

}
