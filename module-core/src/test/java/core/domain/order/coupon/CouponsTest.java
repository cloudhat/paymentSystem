package core.domain.order.coupon;

import core.domain.member.entity.Member;
import core.domain.order.entity.Orders;
import core.domain.order.entity.coupon.Coupon;
import core.domain.order.entity.coupon.Coupons;
import core.domain.order.entity.orderPriceHistory.OrderPriceHistory;
import core.domain.CouponFixture;
import core.domain.MemberFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class CouponsTest {

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
    void setGivenData() {
        Member member = MemberFixture.getMember();

        fixedCoupon = CouponFixture.getFixedCoupon(member);
        rateCoupon = CouponFixture.getRateCoupon(member);
        duplicateAvailableFiexedCoupon = CouponFixture.getDuplicateAvailableFiexedCoupon(member);
        duplicateAvailableRateCoupon = CouponFixture.getDuplicateAvailableRateCoupon(member);
        duplicateAvailableExpiredCoupon = CouponFixture.getDuplicateAvailableExpiredCoupon(member);
        duplicateAvailableUsedCoupon = CouponFixture.getDuplicateAvailableUsedCoupon(member);
        bigMaxDiscountAmountrateCoupon = CouponFixture.getBigMaxDiscountAmountrateCoupon(member);
    }

    @DisplayName("비율쿠폰의 할인율이 최대할인가능액수보다 클 경우 ")
    @Test
    void validCouponsCase1() {
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
    void validCouponsCase2() {
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
    void inValidCase1() {
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
    void inValidCase11() {
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
    void inValidCase2() {
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
    void inValidCase3() {
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
    void inValidCase4() {
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
    void inValidCase5() {

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
    void noCoupon() {
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
