package com.paymentsystemex.domain.payment;

import lombok.Getter;

@Getter
public enum PaymentStatus {
    BEFORE_PAYMENT("결제 전"),
    STARTED("결제 시작"),
    FAIL("결제 실패"),
    COMPLETE("결제 완료"),
    CANCEL("결제 취소");

    private final String status;

    PaymentStatus(String status) {
        this.status = status;
    }
}
