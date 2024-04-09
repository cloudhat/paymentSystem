package com.paymentsystemex.util.price;

import com.paymentsystemex.domain.member.address.Address;

public class DeliveryFeePolicy extends PricePolicy {

    private Address address;

    public DeliveryFeePolicy(Address address) {
        this.address = address;
    }

    @Override
    public int calculatePrice(int price) {
        return price + address.getDeliveryCharge().getAmount();
    }
}
