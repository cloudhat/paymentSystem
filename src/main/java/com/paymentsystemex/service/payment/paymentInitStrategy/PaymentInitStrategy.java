package com.paymentsystemex.service.payment.paymentInitStrategy;

import com.paymentsystemex.domain.member.Member;
import com.paymentsystemex.domain.order.OrderCart;
import com.paymentsystemex.domain.order.OrderPriceHistory;
import com.paymentsystemex.domain.order.Orders;
import com.paymentsystemex.domain.payment.Payment;
import com.paymentsystemex.dto.payment.PaymentRequest;
import com.paymentsystemex.repository.CartRepository;
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
