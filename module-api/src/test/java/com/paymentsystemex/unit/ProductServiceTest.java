package com.paymentsystemex.unit;

import com.paymentsystemex.domain.payment.service.PaymentService;
import com.paymentsystemex.domain.product.service.ProductService;
import com.paymentsystemex.utils.JpaH2TestBase;
import core.domain.ProductFixture;
import core.domain.order.entity.orderProduct.OrderProduct;
import core.domain.product.entity.Product;
import core.domain.product.entity.ProductOption;
import core.domain.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class ProductServiceTest extends JpaH2TestBase {

    @Autowired
    private ProductService productService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private ProductRepository productRepository;


    private Long productOption1Id;
    private Long productOption2Id;

    private static int PRODUCT1_OPTION_QUANTITY = ProductFixture.QUANTITY_OF_AVAILABLE_PRODUCT_OPTION;

    private OrderProduct orderProduct1;
    private OrderProduct orderProduct2;

    @BeforeEach
    void setGivenData() {

        Product product = productRepository.saveProduct(ProductFixture.getProduct1());

        ProductOption productOption1 = ProductFixture.getAvailableProductOption1(product);
        ProductOption productOption2 = ProductFixture.getAvailableProductOption1(product);
        productOption1Id = productRepository.saveProductOption(productOption1).getId();
        productOption2Id = productRepository.saveProductOption(productOption2).getId();
        PRODUCT1_OPTION_QUANTITY = ProductFixture.QUANTITY_OF_AVAILABLE_PRODUCT_OPTION;

        orderProduct1 = new OrderProduct(null, "상품1", "상품옵션1", 1, 10000, null, null, productOption1);
        orderProduct2 = new OrderProduct(null, "상품1", "상품옵션1", 1, 10000, null, null, productOption2);
    }

    @DisplayName("상품 단건 수량 변경")
    @Test
    void updateQuantity() {
        //given
        List<OrderProduct> orderProductList = Arrays.asList(orderProduct1);
        for (int i = 0; i < PRODUCT1_OPTION_QUANTITY; i++) {
            productService.updateQuantity(orderProductList);
        }

        //when & then
        assertThatThrownBy(() -> productService.updateQuantity(orderProductList))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Quantity cannot be negative");
        ProductOption productOption1 = productRepository.findProductOptionById(productOption1Id).get();
        assertThat(productOption1.getQuantity()).isZero();

    }

}
