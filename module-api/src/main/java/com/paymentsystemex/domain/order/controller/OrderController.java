package com.paymentsystemex.domain.order.controller;

import com.paymentsystemex.domain.order.dto.*;
import com.paymentsystemex.global.auth.principal.AuthenticationPrincipal;
import com.paymentsystemex.global.auth.principal.UserPrincipal;
import com.paymentsystemex.domain.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderFormResponse> createOrder(@AuthenticationPrincipal UserPrincipal userPrincipal, @RequestBody OrderRequest orderRequest) {
        return ResponseEntity.ok(orderService.saveOrder(orderRequest, userPrincipal));
    }

    @GetMapping("/checkout/{idempotencyKey}")
    public ResponseEntity<OrderFormResponse> showOrderForm(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable String idempotencyKey) {
        return ResponseEntity.ok().body(orderService.getOrderResponse(userPrincipal, idempotencyKey));

    }

    @GetMapping
    public ResponseEntity<OrderHistoryResponse> showOrderHistoryList(@AuthenticationPrincipal UserPrincipal userPrincipal, OrderHistoryRequest orderHistoryRequest) {
        return ResponseEntity.ok().body(orderService.getOrderHistoryResponse(userPrincipal, orderHistoryRequest));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> showOrderHistory(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrderResponse(userPrincipal, orderId));
    }

    @PatchMapping("/cancel/{orderId}")
    public ResponseEntity<OrderResponse> cancel(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long orderId){
        return ResponseEntity.ok(orderService.cancelOrder(userPrincipal, orderId));
    }

}
