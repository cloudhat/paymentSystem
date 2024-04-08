package com.paymentsystemex.controller;


import com.paymentsystemex.auth.principal.AuthenticationPrincipal;
import com.paymentsystemex.auth.principal.UserPrincipal;
import com.paymentsystemex.domain.Cart;
import com.paymentsystemex.dto.cart.CartRequest;
import com.paymentsystemex.dto.cart.CartResponse;
import com.paymentsystemex.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/carts")
public class CartController {

    private final CartService cartService;

    @PostMapping
    public ResponseEntity<Void> createCart(@RequestBody CartRequest cartRequest, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        Cart cart = cartService.saveCart(cartRequest, userPrincipal);
        return ResponseEntity.created(URI.create("/carts/" + cart.getId())).build();
    }

    @GetMapping
    public ResponseEntity<List<CartResponse>> shorCarts(@RequestBody CartRequest cartRequest , @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok().body(cartService.getCartResponses(userPrincipal));
    }

}

