package com.paymentsystemex.acceptance;

import com.paymentsystemex.acceptance.commonStep.CartStep;
import com.paymentsystemex.acceptance.commonStep.MemberSteps;
import com.paymentsystemex.acceptance.commonStep.OrderStep;
import com.paymentsystemex.domain.order.OrderCart;
import com.paymentsystemex.domain.order.OrderProduct;
import com.paymentsystemex.domain.product.Product;
import com.paymentsystemex.domain.product.ProductOption;
import com.paymentsystemex.dto.cart.CartResponse;
import com.paymentsystemex.dto.order.OrderCartResponse;
import com.paymentsystemex.repository.ProductOptionRepository;
import com.paymentsystemex.repository.ProductRepository;
import com.paymentsystemex.utils.AcceptanceTest;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class OrdersAcceptanceTest extends AcceptanceTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductOptionRepository productOptionRepository;


    private static String accessToken;

    private static final int PRODUCT1_PRICE = 20000;
    private static final int PRODUCT2_PRICE = 10000;

    private Long cart1Id;
    private Long cart2Id;

    @BeforeEach
    public void setGivenData() {

        MemberSteps.회원_생성_요청("email1@email.com", "password1", 20);
        accessToken = MemberSteps.로그인_요청("email1@email.com", "password1");

        Product product1 = new Product(null, null, "검정티셔츠");
        Product product2 = new Product(null, null, "초록맨투맨");
        productRepository.save(product1);
        productRepository.save(product2);

        ProductOption productOption1 = new ProductOption(null, product1, "L 사이즈", PRODUCT1_PRICE, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1));
        ProductOption productOption2 = new ProductOption(null, product1, "M 사이즈", PRODUCT2_PRICE, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1));
        productOptionRepository.save(productOption1);
        productOptionRepository.save(productOption2);

        cart1Id = CartStep.장바구니_생성(accessToken, product1.getId(), productOption1.getId(), 1).jsonPath().getLong("id");
        cart2Id = CartStep.장바구니_생성(accessToken, product2.getId(), productOption2.getId(), 2).jsonPath().getLong("id");

    }

    @DisplayName("주문정보를 생성하고 조회한다")
    @Test
    public void creatOrder() {
        //when
        String idempotencyKey = OrderStep.주문_생성(accessToken, Arrays.asList(cart1Id, cart2Id)).jsonPath().getString("idempotencyKey");
        ExtractableResponse<Response> response = OrderStep.내_주문_조회(accessToken, idempotencyKey);
        List<OrderCartResponse> orderCarts = response.jsonPath().getList("orderCartResponses", OrderCartResponse.class);

        //then
        assertThat(orderCarts.stream().map(orderCart -> orderCart.getCartId()).toList()).contains(cart1Id, cart2Id);

    }

}
