package com.paymentsystemex.domain.payment;

import lombok.Getter;

@Getter
public enum PaymentStatus {
    BEFORE_PAYMENT,
    STARTED,
    FAIL,
    COMPLETE,
    CANCEL,
    PARTIAL_REFUND,
    REFUND;
}
