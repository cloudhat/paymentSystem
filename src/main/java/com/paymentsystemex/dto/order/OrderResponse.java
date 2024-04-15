package com.paymentsystemex.dto.order;

import com.paymentsystemex.domain.order.OrderCart;
import com.paymentsystemex.domain.order.OrderProduct;
import com.paymentsystemex.domain.order.Orders;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
public class OrderResponse {

    private String idempotencyKey;

    private List<OrderProductResponse> orderProductResponses;
    private List<OrderCartResponse> orderCartResponses;

    public static OrderResponse fromEntity(Orders orders, List<OrderProduct> orderProducts, List<OrderCart> orderCarts) {
        OrderResponse orderResponse = new OrderResponse();
        orderResponse.idempotencyKey = orders.getIdempotencyKey();
        orderResponse.orderProductResponses = orderProducts.stream().map(OrderProductResponse::fromEntity).toList();
        orderResponse.orderCartResponses = orderCarts.stream().map(OrderCartResponse::fromEntity).toList();

        return orderResponse;
    }
}
