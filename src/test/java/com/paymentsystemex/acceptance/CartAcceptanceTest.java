package com.paymentsystemex.acceptance;

import com.paymentsystemex.acceptance.commonStep.CartStep;
import com.paymentsystemex.acceptance.commonStep.MemberSteps;
import com.paymentsystemex.domain.product.Product;
import com.paymentsystemex.domain.product.ProductOption;
import com.paymentsystemex.dto.cart.CartResponse;
import com.paymentsystemex.repository.ProductRepository;
import com.paymentsystemex.utils.AcceptanceTest;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CartAcceptanceTest extends AcceptanceTest {

    @Autowired
    private ProductRepository productRepository;

    private static final String EMAIL = "email1@email.com";
    private static final String PASSWORD = "password1";
    private static final int AGE = 20;

    private static final String PRODUCT_NAME = "검정티셔츠";

    private static final String AVAILABLE_PRODUCT_OPTION_NAME = "L 사이즈";


    private Long productId;

    private Long availableProductOptionId;
    private Long unAvailableProductOptionId;

    @BeforeEach
    public void setGivenData() {

        MemberSteps.회원_생성_요청(EMAIL, PASSWORD, AGE);

        Product product = new Product(null, null, PRODUCT_NAME);

        productRepository.saveProduct(product);
        productId = product.getId();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime yesterday = now.minusDays(1);
        LocalDateTime tomorrow = now.plusDays(1);
        ProductOption availableProductOption = new ProductOption(1, null, product, AVAILABLE_PRODUCT_OPTION_NAME, 25000, 1, yesterday, tomorrow);
        ProductOption unAvailableProductOption = new ProductOption(1, null, product, "M 사이즈", 35000, 1, tomorrow, tomorrow);
        productRepository.saveProductOption(availableProductOption);
        productRepository.saveProductOption(unAvailableProductOption);

        availableProductOptionId = availableProductOption.getId();
        unAvailableProductOptionId = unAvailableProductOption.getId();
    }


    /**
     * Given 로그인을 하고
     * When 장바구니 생성을 요청하면
     * Then 장바구니가 엔티티가 생성된다
     */
    @DisplayName("신규 장바구니를 생성 및 조회한다")
    @Test
    void createCart() {
        //given
        String accessToken = MemberSteps.로그인_요청(EMAIL, PASSWORD);
        int buyQuantity = 2;

        //when
        CartStep.장바구니_생성(accessToken, productId, availableProductOptionId, buyQuantity);
        ExtractableResponse<Response> response = CartStep.내_장바구니_목록_전체조회(accessToken);
        List<CartResponse> cartResponseList = response.jsonPath().getList("", CartResponse.class);

        //then
        assertThat(cartResponseList.get(0).getProductName()).isEqualTo(PRODUCT_NAME);
        assertThat(cartResponseList.get(0).getProductOptionName()).isEqualTo(AVAILABLE_PRODUCT_OPTION_NAME);
        assertThat(cartResponseList.get(0).getQuantity()).isEqualTo(buyQuantity);
    }


    /**
     * Given 특정 상품에 대해 장바구니를 생성하고
     * When 해당 상품에 대해 다시 장바구니를 생성 요청하면
     * Then 이미 생성된 장바구니의 재고를 업데이트 한다
     */
    @DisplayName("이미 담긴 상품을 다시 장바구니 생성 요청한다")
    @Test
    void createCartAlreadyExists() {
        //given
        String accessToken = MemberSteps.로그인_요청(EMAIL, PASSWORD);
        int firstBuyQuantity = 2;
        int secondBuyQuantity = 5;
        CartStep.장바구니_생성(accessToken, productId, availableProductOptionId, firstBuyQuantity);


        //when
        CartStep.장바구니_생성(accessToken, productId, availableProductOptionId, secondBuyQuantity);


        //then
        ExtractableResponse<Response> response = CartStep.내_장바구니_목록_전체조회(accessToken);
        List<CartResponse> cartResponseList = response.jsonPath().getList("", CartResponse.class);

        assertThat(cartResponseList.get(0).getQuantity()).isEqualTo(firstBuyQuantity + secondBuyQuantity);
    }

    /**
     * Given 로그인을 하고
     * When 판매기간이 지난 상품에 대해 장바구니 생성을 요청할 경우
     * Then 400 에러 발생
     */
    @DisplayName("판매기간이 지난 상품옵션으로 장바구니 생성 요청한다")
    @Test
    void createCartUnAvaiilable() {
        //given
        String accessToken = MemberSteps.로그인_요청(EMAIL, PASSWORD);

        //when
        ExtractableResponse<Response> response = CartStep.장바구니_생성(accessToken, productId, unAvailableProductOptionId, 3);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 장바구니를 생성하고
     * When  장바구니 수량을 변경하면
     * Then  변경에 성공한다
     */
    @DisplayName("카트의 수량을 변경한다")
    @Test
    void updateQuantity() {
        //given
        String accessToken = MemberSteps.로그인_요청(EMAIL, PASSWORD);
        int initialQuantity = 4;
        int changeAmount1 = -2;
        int changeAmount2 = 3;

        Long cartId = CartStep.장바구니_생성(accessToken, productId, availableProductOptionId, initialQuantity).jsonPath().getLong("id");


        //when
        CartStep.장바구니_수량_변경(accessToken, cartId, changeAmount1);
        CartStep.장바구니_수량_변경(accessToken, cartId, changeAmount2);

        //then
        ExtractableResponse<Response> response = CartStep.내_장바구니_목록_전체조회(accessToken);
        List<CartResponse> cartResponseList = response.jsonPath().getList("", CartResponse.class);

        assertThat(cartResponseList.get(0).getQuantity()).isEqualTo(initialQuantity + changeAmount1 + changeAmount2);

    }


    /**
     * Given 특정 장바구니를 생성하고
     * When 해당 장바구니의 수량을 0 이하로 바꾸려고 시도하면
     * Then 400 Response 발생
     */
    @DisplayName("카트의 수량을 0 이하로 변경한다")
    @Test
    void updateQuantityToMinus() {
        //given
        String accessToken = MemberSteps.로그인_요청(EMAIL, PASSWORD);
        int initialQuantity = 3;

        Long cartId = CartStep.장바구니_생성(accessToken, productId, availableProductOptionId, initialQuantity).jsonPath().getLong("id");


        //when
        ExtractableResponse<Response> response = CartStep.장바구니_수량_변경(accessToken, cartId, -initialQuantity);


        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 장바구니를 생성하고
     * When  삭제 요청을 하면
     * Then  삭제에 성공한다
     */
    @DisplayName("장바구니를 삭제한다")
    @Test
    void deleteCart() {
        //given
        String accessToken = MemberSteps.로그인_요청(EMAIL, PASSWORD);
        Long cartId = CartStep.장바구니_생성(accessToken, productId, availableProductOptionId, 3).jsonPath().getLong("id");


        //when
        ExtractableResponse<Response> response = RestAssured.given()
                .header("Authorization", "Bearer " + accessToken)
                .when().delete("/carts/" + cartId)
                .then().extract();


        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        List<CartResponse> cartResponseList = CartStep.내_장바구니_목록_전체조회(accessToken).jsonPath().getList("", CartResponse.class);
        assertThat(cartResponseList).isEmpty();

    }

}
