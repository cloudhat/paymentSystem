package com.paymentsystemex.controller;

import com.paymentsystemex.auth.principal.AuthenticationPrincipal;
import com.paymentsystemex.auth.principal.UserPrincipal;
import com.paymentsystemex.dto.order.OrderRequest;
import com.paymentsystemex.dto.order.OrderResponse;
import com.paymentsystemex.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@AuthenticationPrincipal UserPrincipal userPrincipal, @RequestBody OrderRequest orderRequest) {
        return ResponseEntity.ok(orderService.saveOrder(orderRequest, userPrincipal));
    }

    @GetMapping("/{idempotencyKey}")
    public ResponseEntity<OrderResponse> showOrder(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable String idempotencyKey) {
        return ResponseEntity.ok().body(orderService.getOrderResponse(userPrincipal, idempotencyKey));

    }
}
