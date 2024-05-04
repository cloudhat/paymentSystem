package com.paymentsystemex.service.payment.paymentInitStrategy;

import com.paymentsystemex.domain.order.OrderPriceHistory;
import com.paymentsystemex.domain.order.Orders;
import com.paymentsystemex.domain.order.PriceType;
import com.paymentsystemex.service.payment.paymentInitStrategy.pricePolicy.PricePolicy;

import java.util.List;

public class NaverPayPolicy extends PricePolicy {

    @Override
    public List<OrderPriceHistory> getOrderPriceHistory(int price, Orders orders) {
        OrderPriceHistory orderPriceHistory = new OrderPriceHistory(orders, PriceType.EVENT, "네이버페이 결제 할인", -2000);
        return List.of(orderPriceHistory);
    }

}
