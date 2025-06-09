package com.paymentsystemex.domain.payment.service.paymentInitStrategy;

import core.domain.member.entity.Member;
import core.domain.order.entity.OrderCart;
import core.domain.order.entity.OrderPriceHistory;
import core.domain.order.entity.Orders;
import core.domain.payment.entity.Payment;
import core.domain.payment.dto.PaymentRequest;
import core.domain.cart.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public abstract class PaymentInitStrategy {

    private final CartRepository cartRepository;

    abstract String getEventType();

    abstract Payment initPayment(PaymentRequest paymentRequest, Member member, Orders orders);

    abstract OrderPriceHistory cancelPayment(Orders orders);

    @Transactional
    public Payment getPayment(PaymentRequest paymentRequest, Member member, Orders orders) {
        Payment payment = this.initPayment(paymentRequest, member, orders);
        deleteCart(member, orders);

        return payment;
    }

    @Transactional
    public OrderPriceHistory initCancel(Orders orders) {
        return cancelPayment(orders);
    }

    private void deleteCart(Member member, Orders orders) {
        List<OrderCart> orderCartList = orders.getOrderCarts();
        List<Long> cartIdList = orderCartList.stream()
                .map(OrderCart::getCartId)
                .collect(Collectors.toList());

        cartRepository.bulkDelete(cartIdList, member.getId());
        orders.getOrderCarts().clear();
    }
}
