package com.paymentsystemex.util.price;

import com.paymentsystemex.domain.coupon.Coupon;

import java.util.List;

public class CouponPricePolicy extends PricePolicy {

    private final int MAX_COUPON_APPLICABLE_COUNT = 2;

    private List<Coupon> coupons;

    public CouponPricePolicy(List<Coupon> coupons) {
        this.coupons = coupons;
    }

    @Override
    public int calculatePrice(int price) {
        return coupons.getDiscountedPrice(price);
    }

    private void isDiscountCouponApplicable(List<Coupon> coupons) {

        if (coupons.size() > MAX_COUPON_APPLICABLE_COUNT) {
            throw new IllegalArgumentException("Exceeded maximum number of coupons applicable");
        }


        if (coupons.size() >= MAX_COUPON_APPLICABLE_COUNT) {
            int duplicationAllowedCount = (int) coupons.stream()
                    .filter(Coupon::isDuplicationAllowed)
                    .count();

            if (duplicationAllowedCount < MAX_COUPON_APPLICABLE_COUNT - 1) {
                throw new IllegalArgumentException("Insufficient number of coupons allowed for application");
            }
        }
    }
}
