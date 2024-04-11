package com.paymentsystemex.unit;

import com.paymentsystemex.domain.coupon.Coupon;
import com.paymentsystemex.domain.coupon.CouponType;
import com.paymentsystemex.domain.coupon.Coupons;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class CouponsTest {

    //중복불가쿠폰
    private Coupon fixedCoupon;
    private Coupon rateCoupon;

    //중복가능쿠폰
    private Coupon duplicateAvailableFiexedCoupon;
    private Coupon duplicateAvailableRateCoupon;
    private Coupon duplicateAvailableExpiredCoupon;
    private Coupon duplicateAvailableUsedCoupon;

    private int MIN_PURCHASE_AMOUNT = 10000;
    private int DISCOUNT_AMOUNT = 1000;
    private int DISCOUNT_RATE = 20;
    private int
    @BeforeEach
    public void setGivenData() {

        LocalDateTime now = LocalDateTime.now();

        fixedCoupon = Coupon.builder()
                .expireDt(now.plusDays(1))
                .couponUsed(false)
                .duplicationAllowed(false)
                .couponType(CouponType.FIXED)
                .minPurchaseAmount(MIN_PURCHASE_AMOUNT)
                .build();

        rateCoupon = Coupon.builder()
                .expireDt(now.plusDays(1))
                .couponUsed(false)
                .duplicationAllowed(false)
                .couponType(CouponType.RATE)
                .minPurchaseAmount(MIN_PURCHASE_AMOUNT)
                .build();

        duplicateAvailableFiexedCoupon = Coupon.builder()
                .expireDt(now.plusDays(1))
                .couponUsed(false)
                .duplicationAllowed(true)
                .couponType(CouponType.FIXED)
                .minPurchaseAmount(MIN_PURCHASE_AMOUNT)
                .build();

        duplicateAvailableRateCoupon = Coupon.builder()
                .expireDt(now.plusDays(1))
                .couponUsed(false)
                .duplicationAllowed(true)
                .couponType(CouponType.RATE)
                .minPurchaseAmount(MIN_PURCHASE_AMOUNT)
                .build();

        duplicateAvailableExpiredCoupon = Coupon.builder()
                .expireDt(now.minusDays(1))
                .couponUsed(false)
                .duplicationAllowed(true)
                .couponType(CouponType.RATE)
                .minPurchaseAmount(MIN_PURCHASE_AMOUNT)
                .build();

        duplicateAvailableUsedCoupon = Coupon.builder()
                .expireDt(now.plusDays(1))
                .couponUsed(true)
                .duplicationAllowed(true)
                .couponType(CouponType.RATE)
                .minPurchaseAmount(MIN_PURCHASE_AMOUNT)
                .build();
    }

    @DisplayName("정상적인 할인쿠폰 목록을 생성 case")
    @Test
    public void validCoupons() {
        //given
        List<Coupon> couponList = new ArrayList<>();
        couponList.add(duplicateAvailableFiexedCoupon);
        couponList.add(rateCoupon);
        int totalPrice = MIN_PURCHASE_AMOUNT + 10000;

        //when
        Coupons coupons = new Coupons(couponList, totalPrice);

        //then
        assertThat(coupons.getDiscountedPrice(totalPrice)).isEqualTo()
    }
}
