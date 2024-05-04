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
