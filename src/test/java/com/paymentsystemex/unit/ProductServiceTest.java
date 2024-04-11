package com.paymentsystemex.unit;

import com.paymentsystemex.domain.order.OrderProduct;
import com.paymentsystemex.domain.product.ProductOption;
import com.paymentsystemex.repository.ProductRepository;
import com.paymentsystemex.service.ProductService;
import com.paymentsystemex.service.payment.PaymentService;
import com.paymentsystemex.utils.AcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class ProductServiceTest extends AcceptanceTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private ProductRepository productRepository;


    private Long productOption1Id;
    private Long productOption2Id;

    private static final int PRODUCT1_OPTION_QUANTITY = 10;
    private static final int PRODUCT2_OPTION_QUANTITY = 10;

    private OrderProduct orderProduct1;
    private OrderProduct orderProduct2;

    @BeforeEach
    public void setGivenData() {
        ProductOption productOption1 = new ProductOption(1, null, null, "상품옵션1", 10000, PRODUCT1_OPTION_QUANTITY, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1));
        ProductOption productOption2 = new ProductOption(1, null, null, "상품옵션2", 10000, PRODUCT2_OPTION_QUANTITY, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1));
        productOption1Id = productRepository.saveProductOption(productOption1).getId();
        productOption2Id = productRepository.saveProductOption(productOption2).getId();

        orderProduct1 = new OrderProduct(null, "상품1", "상품옵션1", 1, 10000, null, null, productOption1);
        orderProduct2 = new OrderProduct(null, "상품1", "상품옵션1", 1, 10000, null, null, productOption2);
    }

    @DisplayName("상품 단건 수량 변경")
    @Test
    public void updateQuantity() {
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

    @DisplayName("동시성 테스트")
    @Test
    public void concurrencyTest() throws InterruptedException {
        //given
        List<OrderProduct> orderProductList = Arrays.asList(orderProduct1, orderProduct2);

        //when
        int numberOfThreads = 100;

        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        for (int i = 0; i < numberOfThreads; i++) {
            executorService.submit(() -> {
                try {
                    paymentService.tryUpdateQuantityTwice(orderProductList);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        //then
        ProductOption productOption1 = productRepository.findProductOptionById(productOption1Id).get();
        ProductOption productOption2 = productRepository.findProductOptionById(productOption2Id).get();

        assertThat(productOption1.getQuantity()).isZero();
        assertThat(productOption2.getQuantity()).isZero();
    }

}
