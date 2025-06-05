package com.paymentsystemex.domain.payment.service.payService;

import core.domain.payment.entity.PaymentMethod;

public interface PayService {

    PaymentMethod getPaymentMethod();


    void requestTransaction(Long paymentId, String payKey, int totalPayAmount) throws Exception;

    void requestCancelTransaction(Long paymentId, String payKey, int totalPayAmount) throws Exception;
}
