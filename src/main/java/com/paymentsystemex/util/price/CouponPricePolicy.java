package com.paymentsystemex.util.price;

import com.paymentsystemex.domain.coupon.Coupons;

public class CouponPricePolicy extends PricePolicy {

    private Coupons coupons;

    public CouponPricePolicy(Coupons coupons) {
        this.coupons = coupons;
    }

    @Override
    public int calculatePrice(int price) {
        return coupons.getDiscountedPrice(price);
    }

}
