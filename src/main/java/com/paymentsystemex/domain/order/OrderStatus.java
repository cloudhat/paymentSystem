package com.paymentsystemex.domain.order;

import lombok.Getter;

@Getter
public enum OrderStatus {
    BEFORE_PAYMENT("결제 전"),
    REGULAR_DELIVERY_ING("정기배송 진행 중"),
    ORDER_COMPLETE("주문 완료");

    private final String status;

    OrderStatus(String status) {
        this.status = status;
    }
}
