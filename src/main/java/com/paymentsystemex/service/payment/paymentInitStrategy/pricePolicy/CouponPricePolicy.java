package com.paymentsystemex.service.payment.paymentInitStrategy.pricePolicy;

import com.paymentsystemex.domain.coupon.Coupons;
import com.paymentsystemex.domain.order.OrderPriceHistory;
import com.paymentsystemex.domain.order.Orders;

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
