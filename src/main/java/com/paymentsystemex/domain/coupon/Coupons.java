package com.paymentsystemex.domain.coupon;

import java.util.List;

/**
 * 할인쿠폰 목록에 대한 일급컬렉션
 */

public class Coupons {
    private final List<Coupon> coupons;

    //쿠폰은 최대 2개만 적용 가능
    private final int MAX_COUPON_APPLICABLE_COUNT = 2;

    //중복적용 불가 쿠폰은 1개만 적용가능
    private final int MAX_NOT_DUPLICATED_COUPON_COUNT = 1;

    //비율할인 쿠폰은 1개만 적용가능
    private final int MAX_RATE_COUPON_COUNT = 1;

    public int getDiscountedPrice(int price) {

        List<Coupon> fixedCoupons = coupons.stream()
                .filter(coupon -> CouponType.FIXED.equals(coupon.getCouponType()))
                .toList();

        List<Coupon> rateCoupons = coupons.stream()
                .filter(coupon -> CouponType.RATE.equals(coupon.getCouponType())).toList();

        for (Coupon coupon : fixedCoupons) {
            price = coupon.discount(price);
        }

        for (Coupon coupon : rateCoupons) {
            price = coupon.discount(price);
        }

        return price;
    }

    public Coupons(List<Coupon> coupons, int totalPrice) {
        validateAvailable(coupons);
        validateMinPurchaseAmount(coupons, totalPrice);
        validateSize(coupons);
        validateDuplicateAvailable(coupons);
        validateRateCouponCount(coupons);
        this.coupons = coupons;
    }

    private void validateAvailable(List<Coupon> coupons) {
        boolean anyUnavailable = coupons.stream()
                .anyMatch(coupon -> !coupon.isAvailable());

        if (anyUnavailable) {
            throw new IllegalArgumentException("One or more coupons are unavailable.");
        }
    }

    private void validateMinPurchaseAmount(List<Coupon> coupons, int totalPrice) {
        boolean underMinPurchaseAmount = coupons.stream()
                .anyMatch(coupon -> totalPrice < coupon.getMinPurchaseAmount());

        if (underMinPurchaseAmount) {
            throw new IllegalArgumentException("The order amount is less than the minimum amount eligible for discounts");
        }
    }

    private void validateSize(List<Coupon> coupons) {
        if (coupons.size() > MAX_COUPON_APPLICABLE_COUNT) {
            throw new IllegalArgumentException("Exceeded maximum number of coupons applicable");
        }
    }

    private void validateDuplicateAvailable(List<Coupon> coupons) {
        int duplicationAllowedCount = (int) coupons.stream()
                .filter(Coupon::isDuplicationAllowed)
                .count();

        if (duplicationAllowedCount < coupons.size() - MAX_NOT_DUPLICATED_COUPON_COUNT) {
            throw new IllegalArgumentException("Exceeded maximum number of not duplication allowed coupons applicable");
        }
    }

    private void validateRateCouponCount(List<Coupon> coupons) {
        int rateCouponCount = (int) coupons.stream()
                .filter(coupon -> CouponType.RATE.equals(coupon.getCouponType()))
                .count();
        if (rateCouponCount > MAX_RATE_COUPON_COUNT) {
            throw new IllegalArgumentException("Exceeded maximum number of rate coupons applicable");

        }
    }


}
