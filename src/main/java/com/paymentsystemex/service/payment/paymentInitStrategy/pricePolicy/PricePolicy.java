package com.paymentsystemex.service.payment.paymentInitStrategy.pricePolicy;

import com.paymentsystemex.domain.order.OrderPriceHistory;
import com.paymentsystemex.domain.order.Orders;

import java.util.ArrayList;
import java.util.List;

public abstract class PricePolicy {
    private PricePolicy nextPricePolicy = null;

    public abstract List<OrderPriceHistory> getOrderPriceHistory(int price, Orders orders);

    public final PricePolicy setNextPricePolicy(PricePolicy nextPricePolicy) {

        PricePolicy pricePolicy = this;
        while (pricePolicy.nextPricePolicy != null) {
            pricePolicy = pricePolicy.nextPricePolicy;
        }
        pricePolicy.nextPricePolicy = nextPricePolicy;

        return this;
    }

    public List<OrderPriceHistory> getOrderPriceList(int price, Orders orders) {
        List<OrderPriceHistory> orderPriceHistoryList = new ArrayList<>();

        PricePolicy pricePolicy = this;
        while (pricePolicy != null) {
            List<OrderPriceHistory> subOrderPriceHistoryList = pricePolicy.getOrderPriceHistory(price, orders);

            price += subOrderPriceHistoryList.stream()
                    .mapToInt(OrderPriceHistory::getAmount)
                    .sum();

            orderPriceHistoryList.addAll(subOrderPriceHistoryList);

            pricePolicy = pricePolicy.nextPricePolicy;
        }

        return orderPriceHistoryList;
    }

}
