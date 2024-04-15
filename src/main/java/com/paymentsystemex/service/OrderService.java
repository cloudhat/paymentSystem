package com.paymentsystemex.service;

import com.paymentsystemex.auth.AuthenticationException;
import com.paymentsystemex.auth.principal.UserPrincipal;
import com.paymentsystemex.domain.Cart;
import com.paymentsystemex.domain.member.Member;
import com.paymentsystemex.domain.order.OrderCart;
import com.paymentsystemex.domain.order.OrderProduct;
import com.paymentsystemex.domain.order.Orders;
import com.paymentsystemex.dto.order.OrderRequest;
import com.paymentsystemex.dto.order.OrderResponse;
import com.paymentsystemex.repository.MemberRepository;
import com.paymentsystemex.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
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

    @Transactional
    public OrderResponse saveOrder(OrderRequest orderRequest, UserPrincipal userPrincipal) {
        Member member = memberRepository.findByEmail(userPrincipal.getUsername()).orElseThrow(AuthenticationException::new);

        List<Cart> carts = cartService.getAvailableCarts(member.getId()).stream()
                .filter(cart -> orderRequest.getCartIdList().contains(cart.getId()))
                .toList();

        if (carts.isEmpty()) {
            throw new EntityNotFoundException();
        }

        Orders orders = new Orders(UUID.randomUUID().toString(), member);
        orderRepository.save(orders);

        List<OrderProduct> orderProducts = carts.stream()
                .map(cart -> new OrderProduct(orders, cart)).toList();

        List<OrderCart> orderCarts = carts.stream()
                .map(cart -> new OrderCart(orders, cart.getId())).toList();

        return OrderResponse.fromEntity(orders, orderProducts, orderCarts);
    }

    public OrderResponse getOrderResponse(UserPrincipal userPrincipal, String idempotencyKey) {
        Member member = memberRepository.findByEmail(userPrincipal.getUsername()).orElseThrow(AuthenticationException::new);
        Orders orders = orderRepository.findByIdempotencyKey(idempotencyKey, member.getId()).orElseThrow(EntityNotFoundException::new);


        return OrderResponse.fromEntity(orders, orders.getOrderProducts(), orders.getOrderCarts());
    }

}
