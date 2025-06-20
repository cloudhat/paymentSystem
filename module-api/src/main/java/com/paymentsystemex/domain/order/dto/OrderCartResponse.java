package com.paymentsystemex.domain.order.dto;

import core.domain.order.entity.OrderCart;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class OrderCartResponse {
    private Long id;
    private Long cartId;

    public static OrderCartResponse fromEntity(OrderCart orderCart){
        OrderCartResponse orderCartResponse = new OrderCartResponse();
        orderCartResponse.id = orderCart.getId();
        orderCartResponse.cartId = orderCart.getCartId();

        return orderCartResponse;
    }
}
