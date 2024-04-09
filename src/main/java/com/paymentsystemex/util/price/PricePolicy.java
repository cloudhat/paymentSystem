package com.paymentsystemex.util.price;

public abstract class PricePolicy {
    private PricePolicy nextPricePolicy = null;

    public abstract int calculatePrice(int price);

    public final PricePolicy setNextPricePolicy(PricePolicy nextPricePolicy) {

        PricePolicy pricePolicy = this;
        while (pricePolicy.nextPricePolicy != null) {
            pricePolicy = pricePolicy.nextPricePolicy;
        }
        pricePolicy.nextPricePolicy = nextPricePolicy;

        return this;
    }

    public int getCalculatedPrice(int price) {

        PricePolicy pricePolicy = this;
        while (pricePolicy != null) {
            price = pricePolicy.calculatePrice(price);
            pricePolicy = pricePolicy.nextPricePolicy;
        }

        return price;
    }

}
