package com.paymentsystemex.domain.payment.service.paymentInitStrategy.pricePolicy;

import com.paymentsystemex.domain.coupon.entity.Coupons;
import com.paymentsystemex.domain.order.entity.OrderPriceHistory;
import com.paymentsystemex.domain.order.entity.Orders;

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
