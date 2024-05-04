package com.paymentsystemex.domain.coupon;

import com.paymentsystemex.domain.order.OrderPriceHistory;
import com.paymentsystemex.domain.order.Orders;
import com.paymentsystemex.domain.order.PriceType;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 할인쿠폰 목록에 대한 일급컬렉션
 */

public class Coupons {
    private final List<Coupon> couponList;

    //쿠폰은 최대 2개만 적용 가능
    private static final int MAX_COUPON_APPLICABLE_COUNT = 2;

    //중복적용 불가 쿠폰은 1개만 적용가능
    private static final int MAX_NOT_DUPLICATED_COUPON_COUNT = 1;

    //비율할인 쿠폰은 1개만 적용가능
    private static final int MAX_RATE_COUPON_COUNT = 1;

    public Coupons(List<Coupon> couponList) {
        validateAvailable(couponList);
        validateSize(couponList);
        validateDuplicateAvailable(couponList);
        validateRateCouponCount(couponList);
        this.couponList = couponList;
    }

    /**
     * 아래 메소드는 4가지 작업을 수행한다
     * 0.최소구매금액 만족여부 검증
     * 1.결제가격에서 할인액수 차감
     * 2.OrderPriceHistory 엔티티 생성
     * 3.쿠폰 사용 처리
     */

    public List<OrderPriceHistory> getOrderPriceHistoryAndMarkAsUsed(int price, Orders orders) {

        if(couponList.isEmpty()){
            return new ArrayList<>();
        }

        //0.최소구매금액 만족여부 검증
        validateMinPurchaseAmount(couponList, price);

        List<OrderPriceHistory> orderPriceHistoryList = new ArrayList<>();

        //쿠폰의 적용 순서에 따라 정렬
        List<Coupon> sortedCoupons = couponList.stream()
                .sorted(Comparator.comparing(coupon -> coupon.getCouponType().getApplicationOrder()))
                .toList();

        for (Coupon coupon : sortedCoupons) {
            int discountAmount = coupon.getDiscountAmount(price);

            //1.결제가격에서 할인액수 차감
            price -= discountAmount;

            //2.OrderPriceHistory 엔티티 생성
            OrderPriceHistory orderPriceHistory = new OrderPriceHistory(orders, PriceType.COUPON, coupon.getName(), -discountAmount);
            orderPriceHistoryList.add(orderPriceHistory);

            //3.쿠폰 사용 처리
            coupon.markAsUsed(orders);
        }

        return orderPriceHistoryList;
    }


    private void validateAvailable(List<Coupon> couponList) {
        boolean anyUnavailable = couponList.stream()
                .anyMatch(coupon -> !coupon.isAvailable());

        if (anyUnavailable) {
            throw new IllegalArgumentException("One or more coupons are unavailable");
        }
    }

    private void validateMinPurchaseAmount(List<Coupon> couponList, int totalPrice) {
        boolean underMinPurchaseAmount = couponList.stream()
                .anyMatch(coupon -> totalPrice < coupon.getMinPurchaseAmount());

        if (underMinPurchaseAmount) {
            throw new IllegalArgumentException("The order amount is less than the minimum amount eligible for discounts");
        }
    }

    private void validateSize(List<Coupon> couponList) {
        if (couponList.size() > MAX_COUPON_APPLICABLE_COUNT) {
            throw new IllegalArgumentException("Exceeded maximum number of coupons applicable");
        }
    }

    private void validateDuplicateAvailable(List<Coupon> couponList) {
        int duplicationAllowedCount = (int) couponList.stream()
                .filter(Coupon::isDuplicationAllowed)
                .count();

        if (duplicationAllowedCount < couponList.size() - MAX_NOT_DUPLICATED_COUPON_COUNT) {
            throw new IllegalArgumentException("Exceeded maximum number of not duplication allowed coupons applicable");
        }
    }

    private void validateRateCouponCount(List<Coupon> couponList) {
        int rateCouponCount = (int) couponList.stream()
                .filter(coupon -> CouponType.RATE.equals(coupon.getCouponType()))
                .count();
        if (rateCouponCount > MAX_RATE_COUPON_COUNT) {
            throw new IllegalArgumentException("Exceeded maximum number of rate coupons applicable");

        }
    }


}
