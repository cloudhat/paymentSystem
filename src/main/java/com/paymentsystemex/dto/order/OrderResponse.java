package com.paymentsystemex.dto.order;

import com.paymentsystemex.domain.order.OrderPriceHistory;
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

    private Long orderId;
    private String paymentStatus;

    private List<OrderProductResponse> orderProductResponses;
    private List<OrderPriceHistoryResponse> orderPriceHistoryResponses;

    public static OrderResponse fromEntity(Orders orders) {
        OrderResponse orderResponse = new OrderResponse();
        orderResponse.orderId = orders.getId();
        orderResponse.paymentStatus = orders.getPaymentStatus();
        orderResponse.orderProductResponses = orders.getOrderProducts().stream().map(OrderProductResponse::fromEntityForHistory).toList();
        orderResponse.orderPriceHistoryResponses = orders.getOrderPriceHistories().stream().map(OrderPriceHistoryResponse::fromEntity).toList();

        return orderResponse;
    }

}
