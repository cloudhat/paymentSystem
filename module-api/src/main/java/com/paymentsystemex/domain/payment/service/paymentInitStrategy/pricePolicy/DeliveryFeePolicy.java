package com.paymentsystemex.domain.payment.service.paymentInitStrategy.pricePolicy;

import com.paymentsystemex.domain.member.entity.address.Address;
import com.paymentsystemex.domain.order.entity.OrderPriceHistory;
import com.paymentsystemex.domain.order.entity.Orders;
import com.paymentsystemex.domain.order.entity.PriceType;

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
