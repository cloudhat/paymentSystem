package com.paymentsystemex.domain.order.service;

import com.paymentsystemex.domain.order.dto.OrderFormResponse;
import com.paymentsystemex.domain.order.dto.OrderHistoryResponse;
import com.paymentsystemex.domain.order.dto.OrderResponse;
import com.paymentsystemex.global.auth.AuthenticationException;
import com.paymentsystemex.global.auth.principal.UserPrincipal;
import core.domain.cart.entity.Cart;
import core.domain.member.entity.Member;
import core.domain.order.dto.*;
import core.domain.order.entity.OrderCart;
import core.domain.order.entity.OrderPriceHistory;
import core.domain.order.entity.OrderProduct;
import core.domain.order.entity.Orders;
import core.domain.payment.entity.Payment;
import core.domain.member.repository.MemberRepository;
import core.domain.order.repository.OrderRepository;
import com.paymentsystemex.domain.payment.service.PaymentService;
import com.paymentsystemex.domain.cart.service.CartService;
import com.paymentsystemex.global.messageQueue.DeadLetterQueueService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final CartService cartService;
    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;
    private final PaymentService paymentService;
    private final DeadLetterQueueService deadLetterQueueService;

    @Transactional
    public OrderFormResponse saveOrder(OrderRequest orderRequest, UserPrincipal userPrincipal) {
        Member member = memberRepository.findByEmail(userPrincipal.getUsername()).orElseThrow(AuthenticationException::new);

        List<Cart> carts = cartService.getAvailableCarts(member.getId()).stream()
                .filter(cart -> orderRequest.getCartIdList().contains(cart.getId()))
                .toList();

        if (carts.isEmpty()) {
            throw new EntityNotFoundException();
        }

        Orders orders = new Orders(UUID.randomUUID().toString(), member);
        orderRepository.saveOrders(orders);

        List<OrderProduct> orderProducts = carts.stream()
                .map(cart -> new OrderProduct(orders, cart)).toList();

        List<OrderCart> orderCarts = carts.stream()
                .map(cart -> new OrderCart(orders, cart.getId())).toList();

        return OrderFormResponse.fromEntity(orders, orderProducts, orderCarts);
    }

    public OrderFormResponse getOrderResponse(UserPrincipal userPrincipal, String idempotencyKey) {
        Member member = memberRepository.findByEmail(userPrincipal.getUsername()).orElseThrow(AuthenticationException::new);
        Orders orders = orderRepository.findOrdersByIdempotencyKey(idempotencyKey, member.getId()).orElseThrow(EntityNotFoundException::new);


        return OrderFormResponse.fromEntity(orders, orders.getOrderProducts(), orders.getOrderCarts());
    }

    @Transactional
    public OrderHistoryResponse getOrderHistoryResponse(UserPrincipal userPrincipal, OrderHistoryRequest orderHistoryRequest) {

        Member member = memberRepository.findByEmail(userPrincipal.getUsername()).orElseThrow(AuthenticationException::new);
        Page<Orders> ordersList = orderRepository.findOrdersByMemberId(member.getId(), orderHistoryRequest);

        return OrderHistoryResponse.fromEntity(ordersList);
    }

    @Transactional
    public OrderResponse getOrderResponse(UserPrincipal userPrincipal, Long orderId) {
        Member member = memberRepository.findByEmail(userPrincipal.getUsername()).orElseThrow(AuthenticationException::new);
        Orders orders = orderRepository.findOrdersById(orderId, member.getId()).orElseThrow(EntityNotFoundException::new);

        return OrderResponse.fromEntity(orders);
    }

    @Transactional
    public OrderResponse cancelOrder(UserPrincipal userPrincipal, Long orderId) {
        Member member = memberRepository.findByEmail(userPrincipal.getUsername()).orElseThrow(AuthenticationException::new);
        Orders orders = orderRepository.findOrdersById(orderId, member.getId()).orElseThrow(EntityNotFoundException::new);
        OrderPriceHistory orderPriceHistory = paymentService.initCancel(orders);

        try {
            Payment payment = orders.getNormalPayment();
            paymentService.requestCancelTransaction(payment.getPaymentMethod(), payment.getId(),payment.getPayKey(), orderPriceHistory.getAmount());
        } catch (Exception e) {
            deadLetterQueueService.enqueue(orderPriceHistory);
        }

        return OrderResponse.fromEntity(orders);
    }

}
