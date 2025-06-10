package fixture.core.domain;

import core.domain.member.entity.Member;
import core.domain.order.entity.coupon.Coupon;
import core.domain.order.entity.coupon.CouponType;

import java.time.LocalDateTime;

public class CouponFixture {

    public static final int MIN_PURCHASE_AMOUNT = 10000;
    public static final int DISCOUNT_AMOUNT = 1000;
    public static final int DISCOUNT_RATE = 20;
    public static final int MAX_DISCOUNT_AMOUNT = 2000;

    public static Coupon getFixedCoupon(Member member) {
        return Coupon.builder()
                .member(member)
                .name("테스트쿠폰")
                .expireDt(LocalDateTime.now().plusDays(1))
                .couponUsed(false)
                .duplicationAllowed(false)
                .couponType(CouponType.FIXED)
                .minPurchaseAmount(MIN_PURCHASE_AMOUNT)
                .discountAmount(DISCOUNT_AMOUNT)
                .discountRate(0)
                .maxDiscountAmount(0)
                .build();
    }

    public static Coupon getRateCoupon(Member member) {
        return Coupon.builder()
                .member(member)
                .name("테스트쿠폰")
                .expireDt(LocalDateTime.now().plusDays(1))
                .couponUsed(false)
                .duplicationAllowed(false)
                .couponType(CouponType.RATE)
                .minPurchaseAmount(MIN_PURCHASE_AMOUNT)
                .discountAmount(0)
                .discountRate(DISCOUNT_RATE)
                .maxDiscountAmount(MAX_DISCOUNT_AMOUNT)
                .build();
    }

    public static Coupon getDuplicateAvailableFiexedCoupon(Member member) {
        return Coupon.builder()
                .member(member)
                .name("테스트쿠폰")
                .expireDt(LocalDateTime.now().plusDays(1))
                .couponUsed(false)
                .duplicationAllowed(true)
                .couponType(CouponType.FIXED)
                .minPurchaseAmount(MIN_PURCHASE_AMOUNT)
                .discountAmount(DISCOUNT_AMOUNT)
                .discountRate(0)
                .maxDiscountAmount(0)
                .build();
    }

    public static Coupon getDuplicateAvailableRateCoupon(Member member) {
        return Coupon.builder()
                .member(member)
                .name("테스트쿠폰")
                .expireDt(LocalDateTime.now().plusDays(1))
                .couponUsed(false)
                .duplicationAllowed(true)
                .couponType(CouponType.RATE)
                .minPurchaseAmount(MIN_PURCHASE_AMOUNT)
                .discountAmount(0)
                .discountRate(DISCOUNT_RATE)
                .maxDiscountAmount(MAX_DISCOUNT_AMOUNT)
                .build();
    }

    public static Coupon getDuplicateAvailableExpiredCoupon(Member member) {
        return Coupon.builder()
                .member(member)
                .name("테스트쿠폰")
                .expireDt(LocalDateTime.now().minusDays(1))
                .couponUsed(false)
                .duplicationAllowed(true)
                .couponType(CouponType.RATE)
                .minPurchaseAmount(MIN_PURCHASE_AMOUNT)
                .discountAmount(0)
                .discountRate(DISCOUNT_RATE)
                .maxDiscountAmount(MAX_DISCOUNT_AMOUNT)
                .build();
    }

    public static Coupon getDuplicateAvailableUsedCoupon(Member member) {
        return Coupon.builder()
                .member(member)
                .name("테스트쿠폰")
                .expireDt(LocalDateTime.now().plusDays(1))
                .couponUsed(true)
                .duplicationAllowed(true)
                .couponType(CouponType.RATE)
                .minPurchaseAmount(MIN_PURCHASE_AMOUNT)
                .discountAmount(0)
                .discountRate(DISCOUNT_RATE)
                .maxDiscountAmount(MAX_DISCOUNT_AMOUNT)
                .build();
    }

    public static Coupon getBigMaxDiscountAmountrateCoupon(Member member) {
        return Coupon.builder()
                .member(member)
                .name("테스트쿠폰")
                .expireDt(LocalDateTime.now().plusDays(1))
                .couponUsed(false)
                .duplicationAllowed(false)
                .couponType(CouponType.RATE)
                .minPurchaseAmount(MIN_PURCHASE_AMOUNT)
                .discountAmount(0)
                .discountRate(DISCOUNT_RATE)
                .maxDiscountAmount(1000000)
                .build();
    }


}
