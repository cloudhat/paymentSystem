package com.paymentsystemex.domain.coupon;

import java.util.List;

public class Coupons {
    private final List<Coupon> coupons;

    private final int MAX_COUPON_APPLICABLE_COUNT = 2;

    public Coupons(List<Coupon> coupons) {

        this.coupons = coupons;
    }

    private void validateSize(List<Coupon> coupons){
        if (coupons.size() > MAX_COUPON_APPLICABLE_COUNT) {
            throw new IllegalArgumentException("Exceeded maximum number of coupons applicable");
        }
    }

    private void validateDuplicateAvailabe(List<Coupon> coupons){
        int duplicationAllowedCount = (int) coupons.stream()
                .filter(Coupon::isDuplicationAllowed)
                .count();

        if (coupons.size() >= MAX_COUPON_APPLICABLE_COUNT){
            throw new IllegalArgumentException("Insufficient number of coupons allowed for application");
        }
    }
}
