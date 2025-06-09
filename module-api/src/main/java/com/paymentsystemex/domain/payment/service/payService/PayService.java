package com.paymentsystemex.domain.payment.service.payService;

import core.domain.order.entity.payment.PaymentMethod;

public interface PayService {

    PaymentMethod getPaymentMethod();


    void requestTransaction(Long paymentId, String payKey, int totalPayAmount) throws Exception;

    void requestCancelTransaction(Long paymentId, String payKey, int totalPayAmount) throws Exception;
}
