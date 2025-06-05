package com.paymentsystemex.domain.payment.service.paymentInitStrategy.pricePolicy;

import core.domain.order.entity.OrderPriceHistory;
import core.domain.order.entity.Orders;
import core.domain.order.entity.PriceType;

import java.util.List;

public class NaverPayPolicy extends PricePolicy {

    @Override
    public List<OrderPriceHistory> getOrderPriceHistory(int price, Orders orders) {
        OrderPriceHistory orderPriceHistory = new OrderPriceHistory(orders, PriceType.EVENT, "네이버페이 결제 할인", -2000);
        return List.of(orderPriceHistory);
    }

}
