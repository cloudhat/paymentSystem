package com.paymentsystemex.domain.payment.service.paymentInitStrategy.pricePolicy;

import core.domain.member.entity.address.Address;
import core.domain.order.entity.orderPriceHistory.OrderPriceHistory;
import core.domain.order.entity.Orders;
import core.domain.order.entity.orderPriceHistory.PriceType;

import java.util.List;

public class DeliveryFeePolicy extends PricePolicy {

    private final Address address;

    public DeliveryFeePolicy(Address address) {
        this.address = address;
    }

    @Override
    public List<OrderPriceHistory> getOrderPriceHistory(int price, Orders orders) {
        OrderPriceHistory orderPriceHistory = new OrderPriceHistory(orders, PriceType.DELIVERY_FEE, "배송비", address.getDeliveryCharge().getAmount());
        orders.setAddressDetail(address.getAddressDetail());
        return List.of(orderPriceHistory);
    }
}
