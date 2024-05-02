package com.paymentsystemex.domain.order;

import lombok.Getter;

@Getter
public enum OrderStatus {
    BEFORE_PAYMENT,
    REGULAR_DELIVERY_ING,
    ORDER_COMPLETE,
    CANCEL_COMPLETE;


}
