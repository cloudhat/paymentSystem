package com.paymentsystemex.acceptance;

import com.paymentsystemex.acceptance.commonStep.CartStep;
import com.paymentsystemex.acceptance.commonStep.MemberSteps;
import com.paymentsystemex.acceptance.commonStep.OrderStep;
import com.paymentsystemex.acceptance.commonStep.PaymentStep;
import com.paymentsystemex.domain.payment.dto.PaymentInitResponse;
import com.paymentsystemex.domain.payment.dto.PaymentTransactionResponse;
import com.paymentsystemex.mock.PayTestController;
import com.paymentsystemex.utils.AcceptanceTest;
import core.domain.CouponFixture;
import core.domain.member.entity.Member;
import core.domain.member.entity.address.Address;
import core.domain.member.entity.address.DeliveryCharge;
import core.domain.member.repository.MemberRepository;
import core.domain.order.dto.PaymentRequest;
import core.domain.order.entity.coupon.Coupon;
import core.domain.order.entity.payment.Payment;
import core.domain.order.entity.payment.PaymentMethod;
import core.domain.order.repository.CouponRepository;
import core.domain.order.repository.PaymentRepository;
import core.domain.product.entity.Product;
import core.domain.product.entity.ProductOption;
import core.domain.product.repository.ProductRepository;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentAcceptanceTest extends AcceptanceTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    private static String accessToken;
    private Long memberId;

    private static final int PRODUCT1_PRICE = 20000;
    private static final int PRODUCT2_PRICE = 10000;

    private static final String EMAIL = "email1@email.com";
    private static final int PRODUCT1_ORDER_QUANTITY = 1;
    private static final int PRODUCT2_ORDER_QUANTITY = 2;

    private String orderIdempotencyKey;

    private Long addressIdMetro;
    private Long addressIdJeju;

    private Coupon fixedCoupon;
    private Coupon rateCoupon;

    private String payKey = PayTestController.PAY_KEY;

    @BeforeEach
    void setGivenDataForOrder() {

        MemberSteps.회원_생성_요청(EMAIL, "password1", 20);
        accessToken = MemberSteps.로그인_요청(EMAIL, "password1");
        memberId = memberRepository.findByEmail(EMAIL).get().getId();

        Product product1 = new Product(null, null, "검정티셔츠");
        Product product2 = new Product(null, null, "초록맨투맨");
        productRepository.saveProduct(product1);
        productRepository.saveProduct(product2);

        ProductOption productOption1 = new ProductOption(1, null, product1, "L 사이즈", PRODUCT1_PRICE, 10, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1));
        ProductOption productOption2 = new ProductOption(1, null, product1, "M 사이즈", PRODUCT2_PRICE, 10, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1));
        productRepository.saveProductOption(productOption1);
        productRepository.saveProductOption(productOption2);

        Long cart1Id = CartStep.장바구니_생성(accessToken, product1.getId(), productOption1.getId(), PRODUCT1_ORDER_QUANTITY).jsonPath().getLong("id");
        Long cart2Id = CartStep.장바구니_생성(accessToken, product2.getId(), productOption2.getId(), PRODUCT2_ORDER_QUANTITY).jsonPath().getLong("id");

        orderIdempotencyKey = OrderStep.주문_생성(accessToken, Arrays.asList(cart1Id, cart2Id)).jsonPath().getString("idempotencyKey");

        Member member = memberRepository.findByEmail(EMAIL).get();

        addressIdMetro = memberRepository.save(new Address(null, member, "서울시 동작구 ...", true, DeliveryCharge.METROPOLITAN_AREA)).getId();
        addressIdJeju = memberRepository.save(new Address(null, member, "제주특별자치도 제주시 ...", true, DeliveryCharge.JEJU)).getId();

        Coupon fixedCoupon = CouponFixture.getFixedCoupon(member);
        Coupon duplicateAvailableRateCoupon = CouponFixture.getDuplicateAvailableRateCoupon(member);

        this.fixedCoupon = couponRepository.save(fixedCoupon);
        this.rateCoupon = couponRepository.save(duplicateAvailableRateCoupon);
    }

    @Nested
    @DisplayName("성공 case")
    class SuccessCase {

        @Nested
        @DisplayName("결제 초기화 case")
        class InitPaymentCase {


            /**
             * When   디폴트 결제타입으로, 배송지를 수도권으로(배달비 무료) 결제 초기화를 요청하면
             * Then   정책에 부합하는 총 결제 금액을 응답받는다
             */
            @DisplayName("별도의 할인, 추가비용 없이 결제 초기화 진행 case")
            @Test
            void defaultPayment() {

                //when
                PaymentRequest paymentRequest = new PaymentRequest(orderIdempotencyKey, PaymentMethod.TOSS_PAY, "DEFAULT", new ArrayList<>(), addressIdMetro);
                PaymentInitResponse paymentInitResponse = PaymentStep.결제_생성(accessToken, paymentRequest).jsonPath().getObject("", PaymentInitResponse.class);

                //then
                int expectedTotalPayAmount = PRODUCT1_PRICE * PRODUCT1_ORDER_QUANTITY + PRODUCT2_PRICE * PRODUCT2_ORDER_QUANTITY + DeliveryCharge.METROPOLITAN_AREA.getAmount();
                assertThat(paymentInitResponse.getTotalPayAmount()).isEqualTo(expectedTotalPayAmount);

            }


            /**
             * When   디폴트 결제타입으로, 배송지를 제주도로(배달비 발생) 결제 초기화를 요청하면
             * Then   정책에 부합하는 총 결제 금액을 응답받는다
             */
            @DisplayName("추가 배달비 포함 구매 초기화 진행 case")
            @Test
            void deliveryFeePayment() {

                //when
                PaymentRequest paymentRequest = new PaymentRequest(orderIdempotencyKey, PaymentMethod.TOSS_PAY, "DEFAULT", new ArrayList<>(), addressIdJeju);
                PaymentInitResponse paymentInitResponse = PaymentStep.결제_생성(accessToken, paymentRequest).jsonPath().getObject("", PaymentInitResponse.class);

                //then
                int expectedTotalPayAmount = PRODUCT1_PRICE * PRODUCT1_ORDER_QUANTITY + PRODUCT2_PRICE * PRODUCT2_ORDER_QUANTITY + DeliveryCharge.JEJU.getAmount();
                assertThat(paymentInitResponse.getTotalPayAmount()).isEqualTo(expectedTotalPayAmount);

            }

            @Nested
            @DisplayName("할인쿠폰 초기화 case")
            class CouponDiscountCase {


                /**
                 * When 디폴트 결제타입으로, 고정액수 할인쿠폰을 선택하고, 배송지를 제주도로(배달비 발생) 결제 초기화를 요청하면
                 * Then 정책에 부합하는 총 결제 금액을 응답받는다
                 */
                @DisplayName("고정액수 할인쿠폰을 적용 구매 초기화 진행 case")
                @Test
                void fixedCouponPayment() {

                    //when
                    PaymentRequest paymentRequest = new PaymentRequest(orderIdempotencyKey, PaymentMethod.TOSS_PAY, "DEFAULT", Arrays.asList(fixedCoupon.getId()), addressIdJeju);
                    PaymentInitResponse paymentInitResponse = PaymentStep.결제_생성(accessToken, paymentRequest).jsonPath().getObject("", PaymentInitResponse.class);
                    Payment payment = paymentRepository.findPaymentById(paymentInitResponse.getPaymentId(), memberId).get();

                    //then
                    int productPriceSum = PRODUCT1_PRICE * PRODUCT1_ORDER_QUANTITY + PRODUCT2_PRICE * PRODUCT2_ORDER_QUANTITY;
                    int expectedTotalPayAmount = productPriceSum - fixedCoupon.getDiscountAmount(productPriceSum) + DeliveryCharge.JEJU.getAmount();
                    assertThat(paymentInitResponse.getTotalPayAmount()).isEqualTo(expectedTotalPayAmount);
                    assertThat(payment.getTotalDiscountAmount()).isEqualTo(-fixedCoupon.getDiscountAmount(productPriceSum));
                }


                /**
                 * When 디폴트 결제타입으로, 고정액수 및 비율 할인쿠폰(총 2개)를 선택하고, 배송지를 제주도로(배달비 발생) 결제 초기화를 요청하면
                 * Then 정책에 부합하는 총 결제 금액을 응답받는다
                 */
                @DisplayName("고정,비율 할인쿠폰을 적용 구매 초기화 진행")
                @Test
                void rateCouponPayment() {
                    //given
                    PaymentRequest paymentRequest = new PaymentRequest(orderIdempotencyKey, PaymentMethod.TOSS_PAY, "DEFAULT", Arrays.asList(rateCoupon.getId(), fixedCoupon.getId()), addressIdJeju);

                    //when
                    PaymentInitResponse paymentInitResponse = PaymentStep.결제_생성(accessToken, paymentRequest).jsonPath().getObject("", PaymentInitResponse.class);
                    Payment payment = paymentRepository.findPaymentById(paymentInitResponse.getPaymentId(), memberId).get();

                    //then
                    int productPriceSum = PRODUCT1_PRICE * PRODUCT1_ORDER_QUANTITY + PRODUCT2_PRICE * PRODUCT2_ORDER_QUANTITY;
                    int fixedDiscountedAmount = fixedCoupon.getDiscountAmount(productPriceSum);
                    int rateDiscountAmount = rateCoupon.getDiscountAmount(productPriceSum - fixedDiscountedAmount);
                    int expectedTotalPayAmount = productPriceSum - fixedDiscountedAmount - rateDiscountAmount + DeliveryCharge.JEJU.getAmount();
                    assertThat(paymentInitResponse.getTotalPayAmount()).isEqualTo(expectedTotalPayAmount);
                    assertThat(payment.getTotalDiscountAmount()).isEqualTo(-fixedDiscountedAmount - rateDiscountAmount);

                }

            }


            /**
             * When 네이버페이 할인 결제타입으로, 고정액수 및 비율 할인쿠폰(총 2개)를 선택하고, 배송지를 제주도로(배달비 발생) 결제 초기화를 요청하면
             * Then 정책에 부합하는 총 결제 금액을 응답받는다
             */
            @DisplayName("결제수단 할인 구매 초기화 case (네이버 페이)")
            @Test
            void naverPayPayment() {
                //given
                PaymentRequest paymentRequest = new PaymentRequest(orderIdempotencyKey, PaymentMethod.NAVER_PAY, "NAVER_PAY", Arrays.asList(rateCoupon.getId(), fixedCoupon.getId()), addressIdJeju);

                //when
                PaymentInitResponse paymentInitResponse = PaymentStep.결제_생성(accessToken, paymentRequest).jsonPath().getObject("", PaymentInitResponse.class);

                //then
                int productPriceSum = PRODUCT1_PRICE * PRODUCT1_ORDER_QUANTITY + PRODUCT2_PRICE * PRODUCT2_ORDER_QUANTITY;
                int eventDiscountAmount = 2000;
                int expectedTotalPayAmount = productPriceSum - eventDiscountAmount + DeliveryCharge.JEJU.getAmount();
                assertThat(paymentInitResponse.getTotalPayAmount()).isEqualTo(expectedTotalPayAmount);
            }


        }


        @Nested
        @DisplayName("결제 완료 case")
        class CompletePaymentCase {


            /**
             * Given 결제수단은 토스페이로,결제 초기화를 완료하고
             * When  결제승인을 요청하면
             * Then  결제에 성공한다
             */
            @DisplayName("토스페이 결제 정상 시나리오 진행")
            @Test
            void TossPayTransactionRequest() {
                //given
                PaymentRequest paymentRequest = new PaymentRequest(orderIdempotencyKey, PaymentMethod.TOSS_PAY, "DEFAULT", new ArrayList<>(), addressIdJeju);
                PaymentInitResponse paymentInitResponse = PaymentStep.결제_생성(accessToken, paymentRequest).jsonPath().getObject("", PaymentInitResponse.class);

                //when
                ExtractableResponse<Response> response = PaymentStep.결제_진행(accessToken, paymentInitResponse.getPaymentId(), payKey);

                //then
                assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            }


            /**
             * Given 결제수단은 네이버페이로,결제 초기화를 완료하고
             * When  결제승인을 요청하면
             * Then  결제에 성공한다
             */
            @DisplayName("네이버페이 결제 정상 시나리오 진행")
            @Test
            void NaverPayTransactionRequest() {
                //given
                PaymentRequest paymentRequest = new PaymentRequest(orderIdempotencyKey, PaymentMethod.NAVER_PAY, "DEFAULT", new ArrayList<>(), addressIdJeju);
                PaymentInitResponse paymentInitResponse = PaymentStep.결제_생성(accessToken, paymentRequest).jsonPath().getObject("", PaymentInitResponse.class);

                //when
                ExtractableResponse<Response> response = PaymentStep.결제_진행(accessToken, paymentInitResponse.getPaymentId(), payKey);

                //then
                assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            }


        }
    }

    @Nested
    @DisplayName("실패 case")
    class FailCase {


        /**
         * Given 결제수단은 토스페이로, 결제 초기화를 완료하고
         * When  잘못된 결제키로 결제승인을 요청하면
         * Then  결제에 실패한다
         */
        @DisplayName("토스페이 잘못된 payKey 진행")
        @Test
        void invalidTossPayKeyTransactionRequest() {
            //given
            PaymentRequest paymentRequest = new PaymentRequest(orderIdempotencyKey, PaymentMethod.TOSS_PAY, "DEFAULT", new ArrayList<>(), addressIdJeju);
            PaymentInitResponse paymentInitResponse = PaymentStep.결제_생성(accessToken, paymentRequest).jsonPath().getObject("", PaymentInitResponse.class);

            //when
            ExtractableResponse<Response> response = PaymentStep.결제_진행(accessToken, paymentInitResponse.getPaymentId(), payKey + "INVALID_STRING");
            PaymentTransactionResponse paymentTransactionResponse = response.jsonPath().getObject("", PaymentTransactionResponse.class);

            //then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
            assertThat(paymentTransactionResponse.getReason()).isEqualTo(PayTestController.FAULURE_MSG);

        }


        /**
         * Given 결제수단은 네이버페이로,결제 초기화를 완료하고
         * When  다른 액수에 해당하는 잘못된 결제키로 결제승인을 요청하면
         * Then  결제에 실패한다
         */
        @DisplayName("네이버페이 금액이 다른 결제 시나리오 진행")
        @Test
        void invalidNaverPayKeyTransactionRequest1() {
            //given
            PaymentRequest paymentRequest = new PaymentRequest(orderIdempotencyKey, PaymentMethod.NAVER_PAY, "DEFAULT", new ArrayList<>(), addressIdJeju);
            PaymentInitResponse paymentInitResponse = PaymentStep.결제_생성(accessToken, paymentRequest).jsonPath().getObject("", PaymentInitResponse.class);

            //when
            ExtractableResponse<Response> response = PaymentStep.결제_진행(accessToken, paymentInitResponse.getPaymentId(), PayTestController.INVALID_PAY_KEY);
            PaymentTransactionResponse paymentTransactionResponse = response.jsonPath().getObject("", PaymentTransactionResponse.class);

            //then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
            assertThat(paymentTransactionResponse.getReason()).isEqualTo("결제금액이 상이합니다.");
        }


        /**
         * Given 결제수단은 네이버페이로,결제 초기화를 완료하고
         * When  존재하지 않는 결제키로 결제승인을 요청하면
         * Then  결제에 실패한다
         */
        @DisplayName("네이버페이 존재하지 않는 payKey 시나리오")
        @Test
        void invalidNaverPayKeyTransactionRequest2() {
            //given
            PaymentRequest paymentRequest = new PaymentRequest(orderIdempotencyKey, PaymentMethod.NAVER_PAY, "DEFAULT", new ArrayList<>(), addressIdJeju);
            PaymentInitResponse paymentInitResponse = PaymentStep.결제_생성(accessToken, paymentRequest).jsonPath().getObject("", PaymentInitResponse.class);

            //when
            ExtractableResponse<Response> response = PaymentStep.결제_진행(accessToken, paymentInitResponse.getPaymentId(), payKey + "!");
            PaymentTransactionResponse paymentTransactionResponse = response.jsonPath().getObject("", PaymentTransactionResponse.class);

            //then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
            assertThat(paymentTransactionResponse.getReason()).isEqualTo(PayTestController.FAULURE_MSG);

        }

    }
}
