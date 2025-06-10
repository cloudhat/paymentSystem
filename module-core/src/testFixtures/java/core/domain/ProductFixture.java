package core.domain;

import core.domain.product.entity.Product;
import core.domain.product.entity.ProductOption;

import java.time.LocalDateTime;

public class ProductFixture {

    public static final int QUANTITY_OF_AVAILABLE_PRODUCT_OPTION = 500;
    public static final int PRICE_OF_AVAILABLE_PRODUCT_OPTION1 = 20000;
    public static final int PRICE_OF_AVAILABLE_PRODUCT_OPTION2 = 10000;


    public static Product getProduct1() {
        return new Product("검정티셔츠");
    }

    public static Product getProduct2() {
        return new Product("초록맨투맨");
    }

    public static ProductOption getAvailableProductOption1(Product product) {
        return new ProductOption(product, "L 사이즈", PRICE_OF_AVAILABLE_PRODUCT_OPTION1, QUANTITY_OF_AVAILABLE_PRODUCT_OPTION, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1));
    }

    public static ProductOption getAvailableProductOption2(Product product) {
        return new ProductOption(product, "M 사이즈", PRICE_OF_AVAILABLE_PRODUCT_OPTION2, QUANTITY_OF_AVAILABLE_PRODUCT_OPTION, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1));
    }

    public static ProductOption getUnAvailableProductOption(Product product) {
        return new ProductOption(product, "L 사이즈", PRICE_OF_AVAILABLE_PRODUCT_OPTION2, QUANTITY_OF_AVAILABLE_PRODUCT_OPTION, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1));
    }
}
