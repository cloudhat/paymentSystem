package com.paymentsystemex.unit;

import com.paymentsystemex.domain.coupon.Coupon;
import com.paymentsystemex.domain.coupon.CouponType;
import com.paymentsystemex.domain.coupon.Coupons;
import com.paymentsystemex.domain.member.Member;
import com.paymentsystemex.domain.order.OrderPriceHistory;
import com.paymentsystemex.domain.order.Orders;
import org.checkerframework.checker.units.qual.C;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class CouponsTest {

    //중복불가쿠폰
    private Coupon fixedCoupon;
    private Coupon rateCoupon;

    //중복가능쿠폰
    private Coupon duplicateAvailableFiexedCoupon;
    private Coupon duplicateAvailableRateCoupon;
    private Coupon duplicateAvailableExpiredCoupon;
    private Coupon duplicateAvailableUsedCoupon;
    private Coupon bigMaxDiscountAmountrateCoupon;

    private final int MIN_PURCHASE_AMOUNT = 10000;
    private final int DISCOUNT_AMOUNT = 1000;
    private final int DISCOUNT_RATE = 20;
    private final int MAX_DISCOUNT_AMOUNT = 2000;

    @BeforeEach
    public void setGivenData() {

        LocalDateTime now = LocalDateTime.now();

        fixedCoupon = Coupon.builder()
                .member(new Member())
                .name("테스트쿠폰")
                .expireDt(now.plusDays(1))
                .couponUsed(false)
                .duplicationAllowed(false)
                .couponType(CouponType.FIXED)
                .minPurchaseAmount(MIN_PURCHASE_AMOUNT)
                .discountAmount(DISCOUNT_AMOUNT)
                .discountRate(0)
                .maxDiscountAmount(0)
                .build();

        rateCoupon = Coupon.builder()
                .member(new Member())
                .name("테스트쿠폰")
                .expireDt(now.plusDays(1))
                .couponUsed(false)
                .duplicationAllowed(false)
                .couponType(CouponType.RATE)
                .minPurchaseAmount(MIN_PURCHASE_AMOUNT)
                .discountAmount(0)
                .discountRate(DISCOUNT_RATE)
                .maxDiscountAmount(MAX_DISCOUNT_AMOUNT)
                .build();

        duplicateAvailableFiexedCoupon = Coupon.builder()
                .member(new Member())
                .name("테스트쿠폰")
                .expireDt(now.plusDays(1))
                .couponUsed(false)
                .duplicationAllowed(true)
                .couponType(CouponType.FIXED)
                .minPurchaseAmount(MIN_PURCHASE_AMOUNT)
                .discountAmount(DISCOUNT_AMOUNT)
                .discountRate(0)
                .maxDiscountAmount(0)
                .build();

        duplicateAvailableRateCoupon = Coupon.builder()
                .member(new Member())
                .name("테스트쿠폰")
                .expireDt(now.plusDays(1))
                .couponUsed(false)
                .duplicationAllowed(true)
                .couponType(CouponType.RATE)
                .minPurchaseAmount(MIN_PURCHASE_AMOUNT)
                .discountAmount(0)
                .discountRate(DISCOUNT_RATE)
                .maxDiscountAmount(MAX_DISCOUNT_AMOUNT)
                .build();

        duplicateAvailableExpiredCoupon = Coupon.builder()
                .member(new Member())
                .name("테스트쿠폰")
                .expireDt(now.minusDays(1))
                .couponUsed(false)
                .duplicationAllowed(true)
                .couponType(CouponType.RATE)
                .minPurchaseAmount(MIN_PURCHASE_AMOUNT)
                .discountAmount(0)
                .discountRate(DISCOUNT_RATE)
                .maxDiscountAmount(MAX_DISCOUNT_AMOUNT)
                .build();

        duplicateAvailableUsedCoupon = Coupon.builder()
                .member(new Member())
                .name("테스트쿠폰")
                .expireDt(now.plusDays(1))
                .couponUsed(true)
                .duplicationAllowed(true)
                .couponType(CouponType.RATE)
                .minPurchaseAmount(MIN_PURCHASE_AMOUNT)
                .discountAmount(0)
                .discountRate(DISCOUNT_RATE)
                .maxDiscountAmount(MAX_DISCOUNT_AMOUNT)
                .build();

        bigMaxDiscountAmountrateCoupon = Coupon.builder()
                .member(new Member())
                .name("테스트쿠폰")
                .expireDt(now.plusDays(1))
                .couponUsed(false)
                .duplicationAllowed(false)
                .couponType(CouponType.RATE)
                .minPurchaseAmount(MIN_PURCHASE_AMOUNT)
                .discountAmount(0)
                .discountRate(DISCOUNT_RATE)
                .maxDiscountAmount(1000000)
                .build();
    }

    @DisplayName("비율쿠폰의 할인율이 최대할인가능액수보다 클 경우 ")
    @Test
    public void validCouponsCase1() {
        //given
        List<Coupon> couponList = new ArrayList<>();
        couponList.add(duplicateAvailableFiexedCoupon);
        couponList.add(rateCoupon);
        int totalPrice = MIN_PURCHASE_AMOUNT + 10000;

        //when
        Coupons coupons = new Coupons(couponList);

        //then
        int expectedPrice = (totalPrice - DISCOUNT_AMOUNT) - MAX_DISCOUNT_AMOUNT;
        List<OrderPriceHistory> orderPriceHistoryList = coupons.getOrderPriceHistoryAndMarkAsUsed(totalPrice, new Orders(null, null));
        int totalDiscountAmount = orderPriceHistoryList.stream()
                .mapToInt(OrderPriceHistory::getAmount)
                .sum();

        assertThat(totalDiscountAmount).isEqualTo(expectedPrice - totalPrice);
    }

    @DisplayName("비율쿠폰의 할인율이 최대할인가능액수보다 작을 경우 ")
    @Test
    public void validCouponsCase2() {
        //given
        List<Coupon> couponList = new ArrayList<>();
        couponList.add(duplicateAvailableFiexedCoupon);
        couponList.add(bigMaxDiscountAmountrateCoupon);
        int totalPrice = MIN_PURCHASE_AMOUNT + 10000;

        //when
        Coupons coupons = new Coupons(couponList);

        //then
        int expectedPrice = (totalPrice - DISCOUNT_AMOUNT) * (100 - DISCOUNT_RATE) / 100;
        List<OrderPriceHistory> orderPriceHistoryList = coupons.getOrderPriceHistoryAndMarkAsUsed(totalPrice, new Orders(null, null));
        int totalDiscountAmount = orderPriceHistoryList.stream()
                .mapToInt(OrderPriceHistory::getAmount)
                .sum();

        assertThat(totalDiscountAmount).isEqualTo(expectedPrice - totalPrice);
    }

    @DisplayName("유효기간이 지난 쿠폰 case")
    @Test
    public void inValidCase1() {
        //given
        List<Coupon> couponList = new ArrayList<>();
        couponList.add(duplicateAvailableFiexedCoupon);
        couponList.add(duplicateAvailableExpiredCoupon);

        //when , then
        assertThatThrownBy(() -> new Coupons(couponList))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("One or more coupons are unavailable");
    }

    @DisplayName("이미 사용한 쿠폰 case")
    @Test
    public void inValidCase11() {
        //given
        List<Coupon> couponList = new ArrayList<>();
        couponList.add(duplicateAvailableFiexedCoupon);
        couponList.add(duplicateAvailableUsedCoupon);

        //when , then
        assertThatThrownBy(() -> new Coupons(couponList))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("One or more coupons are unavailable");
    }

    @DisplayName("최소구매급액 미달 case")
    @Test
    public void inValidCase2() {
        //given
        List<Coupon> couponList = new ArrayList<>();
        couponList.add(duplicateAvailableFiexedCoupon);
        couponList.add(rateCoupon);
        int totalPrice = (int) (MIN_PURCHASE_AMOUNT - MIN_PURCHASE_AMOUNT * 0.5);
        Coupons coupons = new Coupons(couponList);

        //when , then
        assertThatThrownBy(() -> coupons.getOrderPriceHistoryAndMarkAsUsed(totalPrice, new Orders(null, null)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("The order amount is less than the minimum amount eligible for discounts");

    }

    @DisplayName("적용가능 개수 초과 case")
    @Test
    public void inValidCase3() {
        //given
        List<Coupon> couponList = new ArrayList<>();
        couponList.add(duplicateAvailableFiexedCoupon);
        couponList.add(duplicateAvailableFiexedCoupon);
        couponList.add(rateCoupon);

        //when , then
        assertThatThrownBy(() -> new Coupons(couponList))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Exceeded maximum number of coupons applicable");
    }

    @DisplayName("중복가능쿠폰 불만족 case")
    @Test
    public void inValidCase4() {
        //given
        List<Coupon> couponList = new ArrayList<>();
        couponList.add(fixedCoupon);
        couponList.add(rateCoupon);

        //when , then
        assertThatThrownBy(() -> new Coupons(couponList))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Exceeded maximum number of not duplication allowed coupons applicable");
    }

    @DisplayName("적용가능비율쿠폰 개수 초과 case")
    @Test
    public void inValidCase5() {

        //given
        List<Coupon> couponList = new ArrayList<>();
        couponList.add(duplicateAvailableRateCoupon);
        couponList.add(rateCoupon);

        //when , then
        assertThatThrownBy(() -> new Coupons(couponList))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Exceeded maximum number of rate coupons applicable");
    }

    @DisplayName("쿠폰을 적용하지 않은 case")
    @Test
    public void noCoupon() {
        List<Coupon> couponList = new ArrayList<>();
        int totalPrice = MIN_PURCHASE_AMOUNT + 10000;

        //when
        Coupons coupons = new Coupons(couponList);

        //then
        List<OrderPriceHistory> orderPriceHistoryList = coupons.getOrderPriceHistoryAndMarkAsUsed(totalPrice, new Orders(null, null));
        int totalDiscountAmount = orderPriceHistoryList.stream()
                .mapToInt(OrderPriceHistory::getAmount)
                .sum();

        assertThat(totalDiscountAmount).isZero();
    }
}
