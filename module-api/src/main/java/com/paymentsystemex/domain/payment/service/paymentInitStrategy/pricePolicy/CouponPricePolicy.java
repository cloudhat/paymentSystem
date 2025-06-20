package com.paymentsystemex.domain.payment.service.paymentInitStrategy.pricePolicy;

import core.domain.coupon.entity.Coupons;
import core.domain.order.entity.OrderPriceHistory;
import core.domain.order.entity.Orders;

import java.util.List;

public class CouponPricePolicy extends PricePolicy {

    private final Coupons coupons;

    public CouponPricePolicy(Coupons coupons) {
        this.coupons = coupons;
    }

    @Override
    public List<OrderPriceHistory> getOrderPriceHistory(int price, Orders orders) {

        return coupons.getOrderPriceHistoryAndMarkAsUsed(price,orders);
    }

}
