package com.paymentsystemex.domain.member.address;

public enum DeliveryCharge {

    METROPOLITAN_AREA(0),
    PROVINCE(1000),
    JEJU(5000);


    private final int deliveryCharge;

    DeliveryCharge(int deliveryCharge) {
        this.deliveryCharge = deliveryCharge;
    }
}
