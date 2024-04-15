package com.paymentsystemex.dto.order;

import com.paymentsystemex.domain.order.OrderProduct;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class OrderProductResponse {

    private Long id;
    private String productName;
    private String productOptionName;
    private int quantity;
    private int price;

    public static OrderProductResponse fromEntity(OrderProduct orderProduct){
        OrderProductResponse orderProductResponse = new OrderProductResponse();
        orderProductResponse.id = orderProduct.getId();
        orderProductResponse.productName = orderProduct.getProductName();
        orderProductResponse.productOptionName = orderProduct.getProductOptionName();
        orderProductResponse.quantity = orderProduct.getQuantity();
        orderProductResponse.price = orderProduct.getPrice();

        return orderProductResponse;
    }
}
