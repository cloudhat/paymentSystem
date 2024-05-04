package com.paymentsystemex.acceptance;

import com.paymentsystemex.acceptance.commonStep.CartStep;
import com.paymentsystemex.acceptance.commonStep.MemberSteps;
import com.paymentsystemex.acceptance.commonStep.OrderStep;
import com.paymentsystemex.acceptance.commonStep.PaymentStep;
import com.paymentsystemex.domain.member.Member;
import com.paymentsystemex.domain.member.address.Address;
import com.paymentsystemex.domain.member.address.DeliveryCharge;
import com.paymentsystemex.domain.payment.PaymentMethod;
import com.paymentsystemex.domain.product.Product;
import com.paymentsystemex.domain.product.ProductOption;
import com.paymentsystemex.dto.order.OrderCartResponse;
import com.paymentsystemex.dto.order.OrderHistoryRequest;
import com.paymentsystemex.dto.order.OrderHistoryResponse;
import com.paymentsystemex.dto.payment.PaymentInitResponse;
import com.paymentsystemex.dto.payment.PaymentRequest;
import com.paymentsystemex.mock.PayTestController;
import com.paymentsystemex.repository.MemberRepository;
import com.paymentsystemex.repository.ProductRepository;
import com.paymentsystemex.utils.AcceptanceTest;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OrdersAcceptanceTest extends AcceptanceTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MemberRepository memberRepository;

    private static String accessToken;

    private static final int PRODUCT1_PRICE = 20000;
    private static final int PRODUCT2_PRICE = 10000;

    Product product1;
    Product product2;

    ProductOption productOption1;
    ProductOption productOption2;

    Long addressIdMetro;

    @BeforeEach
    public void setGivenData() {

        MemberSteps.회원_생성_요청("email1@email.com", "password1", 20);
        accessToken = MemberSteps.로그인_요청("email1@email.com", "password1");

        product1 = new Product(null, null, "검정티셔츠");
        product2 = new Product(null, null, "초록맨투맨");
        productRepository.saveProduct(product1);
        productRepository.saveProduct(product2);

        productOption1 = new ProductOption(0, null, product1, "L 사이즈", PRODUCT1_PRICE, 100, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1));
        productOption2 = new ProductOption(0, null, product1, "M 사이즈", PRODUCT2_PRICE, 100, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1));
        productRepository.saveProductOption(productOption1);
        productRepository.saveProductOption(productOption2);

        Member member = memberRepository.findByEmail("email1@email.com").get();

        addressIdMetro = memberRepository.save(new Address(null, member, "서울시 동작구 ...", true, DeliveryCharge.METROPOLITAN_AREA)).getId();
    }

    public void setGivenDataForHistory(int iter, Product product1, Product product2, ProductOption productOption1, ProductOption productOption2, Long addressIdMetro) {

        for (int i = 0; i < iter; i++) {
            Long cartId1 = CartStep.장바구니_생성(accessToken, product1.getId(), productOption1.getId(), 2).jsonPath().getLong("id");
            Long cartId2 = CartStep.장바구니_생성(accessToken, product2.getId(), productOption2.getId(), 3).jsonPath().getLong("id");
            String orderIdempotencyKey = OrderStep.주문_생성(accessToken, Arrays.asList(cartId1, cartId2)).jsonPath().getString("idempotencyKey");
            PaymentRequest paymentRequest = new PaymentRequest(orderIdempotencyKey, PaymentMethod.TOSS_PAY, "DEFAULT", new ArrayList<>(), addressIdMetro);
            PaymentInitResponse paymentInitResponse = PaymentStep.결제_생성(accessToken, paymentRequest).jsonPath().getObject("", PaymentInitResponse.class);
            PaymentStep.결제_진행(accessToken, paymentInitResponse.getPaymentId(), PayTestController.PAY_KEY);
        }
    }

    /**
     * Given 장바구니를 생성하고
     * When  주문 생성을 요청하면
     * Then  주문 체크아웃 페이지를 위한 데이터를 응답받는다
     */
    @DisplayName("주문정보를 생성하고 조회한다")
    @Test
    void creatOrder() {
        //given
        Long cart1Id = CartStep.장바구니_생성(accessToken, product1.getId(), productOption1.getId(), 1).jsonPath().getLong("id");
        Long cart2Id = CartStep.장바구니_생성(accessToken, product2.getId(), productOption2.getId(), 2).jsonPath().getLong("id");

        //when
        String idempotencyKey = OrderStep.주문_생성(accessToken, Arrays.asList(cart1Id, cart2Id)).jsonPath().getString("idempotencyKey");
        ExtractableResponse<Response> response = OrderStep.내_주문정보_조회(accessToken, idempotencyKey);
        List<OrderCartResponse> orderCarts = response.jsonPath().getList("orderCartResponses", OrderCartResponse.class);

        //then
        assertThat(orderCarts.stream().map(orderCart -> orderCart.getCartId()).toList()).contains(cart1Id, cart2Id);

    }

    /**
     * Given  주문목록을 12개 생성하고
     * When   지금으로부터 한달 기준, 오래된 순으로 주문내역을 요청하면
     * Then   주문내역을 응답받는다
     */
    @DisplayName("한달 기준으로 오래된 순으로 주문내역을 확인한다")
    @Test
    void orderHistories() {

        //given (주문목록을 12개 생성한다
        int numOfOrder = 12;
        setGivenDataForHistory(numOfOrder, product1, product2, productOption1, productOption2, addressIdMetro);

        //when
        OrderHistoryRequest orderHistoryRequest = OrderHistoryRequest.builder()
                .createAtStartDate(LocalDateTime.now().minusMonths(1))
                .createAtEndDate(LocalDateTime.now())
                .pageNum(1)
                .descending(false)
                .build();
        OrderHistoryResponse orderHistoryResponse = OrderStep.내_주문내역_목록_조회(accessToken, orderHistoryRequest).jsonPath().getObject("", OrderHistoryResponse.class);

        //then
        assertThat(orderHistoryResponse.getOrderResponseList()).hasSize(2);
        assertThat(orderHistoryResponse.getTotalElements()).isEqualTo(numOfOrder);
    }

    /**
     * Given  주문목록을 생성하고, 주문목록을 요청하고
     * When   응답받은 주문목록의 주문번호를 이용하여 주문상세내역 조회를 요청하면
     * Then   주문상세내역을 응답받는다
     */
    @DisplayName("주문내역을 이용하여 주문상세를 확인한다.")
    @Test
    void orderHistoryDetail() {

        //given
        setGivenDataForHistory(1, product1, product2, productOption1, productOption2, addressIdMetro);
        OrderHistoryRequest orderHistoryRequest = OrderHistoryRequest.builder()
                .createAtStartDate(LocalDateTime.now().minusMonths(1))
                .createAtEndDate(LocalDateTime.now())
                .pageNum(0)
                .descending(true)
                .build();
        OrderHistoryResponse orderHistoryResponse = OrderStep.내_주문내역_목록_조회(accessToken, orderHistoryRequest).jsonPath().getObject("", OrderHistoryResponse.class);
        Long orderId = orderHistoryResponse.getOrderResponseList().get(0).getOrderId();

        //when
        ExtractableResponse<Response> response = OrderStep.내_주문내역_상세_조회(accessToken, orderId);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    /**
     * Given  주문번호를 요청하고
     * When   응답받은 주문번호로 결제취소를 요청하면
     * Then   결제취소에 성공한다
     */
    @DisplayName("결제를 취소한다")
    @Test
    void cancelTossPay(){
        //given
        setGivenDataForHistory(1, product1, product2, productOption1, productOption2, addressIdMetro);
        OrderHistoryRequest orderHistoryRequest = OrderHistoryRequest.builder()
                .createAtStartDate(LocalDateTime.now().minusMonths(1))
                .createAtEndDate(LocalDateTime.now())
                .pageNum(0)
                .descending(true)
                .build();
        OrderHistoryResponse orderHistoryResponse = OrderStep.내_주문내역_목록_조회(accessToken, orderHistoryRequest).jsonPath().getObject("", OrderHistoryResponse.class);
        Long orderId = orderHistoryResponse.getOrderResponseList().get(0).getOrderId();

        //when
        ExtractableResponse<Response> response = OrderStep.주문_취소(accessToken, orderId);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        OrderStep.내_주문내역_상세_조회(accessToken, orderId);

    }
}
