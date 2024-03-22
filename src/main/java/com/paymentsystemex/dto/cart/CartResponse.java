package com.paymentsystemex.dto.cart;

import com.paymentsystemex.domain.Cart;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CartResponse {
    private Long id;
    private String productName;
    private String productOptionName;
    private int quantity;
    private int price;

    private LocalDateTime saleEndDt;


    public static CartResponse fromEntity(Cart cart){
        CartResponse cartResponse = new CartResponse();
        cartResponse.id = cartResponse.getId();
        cartResponse.quantity = cart.getQuantity();

        cartResponse.productName = cart.getProduct().getName();
        cartResponse.productOptionName = cart.getProductOption().getName();
        cartResponse.price = cart.getProductOption().getPrice();
        cartResponse.saleEndDt = cart.getProductOption().getSaleEndDt();

        return cartResponse;
    }
}
