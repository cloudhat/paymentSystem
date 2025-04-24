package com.paymentsystemex.domain.payment.service.paymentInitStrategy.pricePolicy;

import com.paymentsystemex.domain.order.entity.OrderPriceHistory;
import com.paymentsystemex.domain.order.entity.OrderProduct;
import com.paymentsystemex.domain.order.entity.Orders;
import com.paymentsystemex.domain.order.entity.PriceType;

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
