package com.paymentsystemex.domain.order.dto;

import core.domain.order.entity.OrderCart;
import core.domain.order.entity.orderProduct.OrderProduct;
import core.domain.order.entity.Orders;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
public class OrderFormResponse {

    private String idempotencyKey;

    private List<OrderProductResponse> orderProductResponses;
    private List<OrderCartResponse> orderCartResponses;

    public static OrderFormResponse fromEntity(Orders orders, List<OrderProduct> orderProducts, List<OrderCart> orderCarts) {
        OrderFormResponse orderFormResponse = new OrderFormResponse();
        orderFormResponse.idempotencyKey = orders.getIdempotencyKey();
        orderFormResponse.orderProductResponses = orderProducts.stream().map(OrderProductResponse::fromEntityForForm).toList();
        orderFormResponse.orderCartResponses = orderCarts.stream().map(OrderCartResponse::fromEntity).toList();

        return orderFormResponse;
    }
}
