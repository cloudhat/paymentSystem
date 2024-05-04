package com.paymentsystemex.domain.coupon;

import lombok.Getter;

@Getter
public enum CouponType {

    FIXED(0),
    RATE(1);

    private final int applicationOrder;

    CouponType(int applicationOrder) {
        this.applicationOrder = applicationOrder;
    }
}
