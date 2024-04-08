package com.paymentsystemex.dto.cart;

import com.paymentsystemex.domain.Cart;
import com.paymentsystemex.domain.member.Member;
import com.paymentsystemex.domain.product.Product;
import com.paymentsystemex.domain.product.ProductOption;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class CartRequest {

    private final Long productId;

    private final Long productOptionId;

    private final int quantity;

}
