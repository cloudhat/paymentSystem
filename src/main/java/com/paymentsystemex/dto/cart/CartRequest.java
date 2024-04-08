package com.paymentsystemex.dto.cart;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class CartRequest {

    private final Long productId;

    private final Long productOptionId;

    private final int quantity;

}
