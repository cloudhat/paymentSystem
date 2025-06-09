package com.paymentsystemex.domain.payment.service.paymentInitStrategy.pricePolicy;

import core.domain.order.entity.orderPriceHistory.OrderPriceHistory;
import core.domain.order.entity.orderProduct.OrderProduct;
import core.domain.order.entity.Orders;
import core.domain.order.entity.orderPriceHistory.PriceType;

import java.util.List;

public class ProductPricePolicy extends PricePolicy {

    private final List<OrderProduct> orderProducts;

    public ProductPricePolicy(List<OrderProduct> orderProducts) {
        if (orderProducts.isEmpty()) {
            throw new IllegalArgumentException("At least one orderProducts required");
        }
        this.orderProducts = orderProducts;
    }

    @Override
    public List<OrderPriceHistory> getOrderPriceHistory(int price, Orders orders) {
        int productPriceSum = orderProducts.stream()
                .mapToInt(orderProduct -> orderProduct.getPrice() * orderProduct.getQuantity())
                .sum();
        OrderPriceHistory orderPriceHistory = new OrderPriceHistory(orders, PriceType.PRODUCT, orders.getOrderProductSummary(), productPriceSum);

        return List.of(orderPriceHistory);
    }
}
