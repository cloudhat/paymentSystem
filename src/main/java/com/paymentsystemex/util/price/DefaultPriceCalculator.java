package com.paymentsystemex.util.price;

import com.paymentsystemex.domain.coupon.Coupon;
import com.paymentsystemex.domain.coupon.Coupons;
import com.paymentsystemex.domain.member.address.Address;
import com.paymentsystemex.domain.product.ProductOption;

import java.util.List;

public class DefaultPriceCalculator {

    public static int totalPrice(List<ProductOption> productOptions, Coupons coupons, Address address) {
        int defaultPrice = 0;

        PricePolicy pricePolicy = new ProductPricePolicy(productOptions)
                .setNextPricePolicy(new CouponPricePolicy(coupons))
                .setNextPricePolicy(new DeliveryFeePolicy(address));


        return pricePolicy.getCalculatedPrice(defaultPrice);
    }
}
