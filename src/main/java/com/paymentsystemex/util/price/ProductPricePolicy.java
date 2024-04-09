package com.paymentsystemex.util.price;

import com.paymentsystemex.domain.product.ProductOption;

import java.util.List;

public class ProductPricePolicy extends PricePolicy {

    private List<ProductOption> productOptions;

    public ProductPricePolicy(List<ProductOption> productOptions) {
        this.productOptions = productOptions;
    }

    @Override
    public int calculatePrice(int price) {

        return productOptions.stream()
                .mapToInt(ProductOption::getPrice)
                .sum();
    }
}
