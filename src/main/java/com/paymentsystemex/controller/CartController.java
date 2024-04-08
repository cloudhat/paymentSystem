package com.paymentsystemex.controller;


import com.paymentsystemex.auth.principal.AuthenticationPrincipal;
import com.paymentsystemex.auth.principal.UserPrincipal;
import com.paymentsystemex.dto.cart.CartRequest;
import com.paymentsystemex.dto.cart.CartResponse;
import com.paymentsystemex.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/carts")
public class CartController {

    private final CartService cartService;

    @PostMapping
    public ResponseEntity<CartResponse> createCart(@AuthenticationPrincipal UserPrincipal userPrincipal, @RequestBody CartRequest cartRequest) {
        CartResponse cart = cartService.saveCart(cartRequest, userPrincipal);
        return ResponseEntity.ok(cart);
    }

    @GetMapping
    public ResponseEntity<List<CartResponse>> showCarts(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok().body(cartService.getCartResponses(userPrincipal));
    }

    @PutMapping("/{cartId}")
    public ResponseEntity<CartResponse> updateCart(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long cartId, @RequestBody Integer changeAmount) {
        cartService.updateCart(cartId, userPrincipal, changeAmount);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{cartId}")
    public ResponseEntity<CartResponse> deleteCart(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long cartId) {
        cartService.deleteCart(userPrincipal, cartId);
        return ResponseEntity.noContent().build();
    }
}

