package com.paymentsystemex.acceptance;

import com.paymentsystemex.acceptance.commonStep.CartStep;
import com.paymentsystemex.acceptance.commonStep.MemberSteps;
import com.paymentsystemex.acceptance.commonStep.OrderStep;
import com.paymentsystemex.acceptance.commonStep.PaymentStep;
import com.paymentsystemex.domain.coupon.Coupon;
import com.paymentsystemex.domain.coupon.CouponType;
import com.paymentsystemex.domain.member.Member;
import com.paymentsystemex.domain.member.address.Address;
import com.paymentsystemex.domain.member.address.DeliveryCharge;
import com.paymentsystemex.domain.payment.Payment;
import com.paymentsystemex.domain.payment.PaymentMethod;
import com.paymentsystemex.domain.product.Product;
import com.paymentsystemex.domain.product.ProductOption;
import com.paymentsystemex.dto.payment.PaymentRequest;
import com.paymentsystemex.dto.payment.PaymentResponse;
import com.paymentsystemex.repository.CouponRepository;
import com.paymentsystemex.repository.MemberRepository;
import com.paymentsystemex.repository.OrderRepository;
import com.paymentsystemex.repository.ProductRepository;
import com.paymentsystemex.utils.AcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class PaymentAcceptanceTest extends AcceptanceTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private OrderRepository orderRepository;

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

    @BeforeEach
    public void setGivenDataForOrder() {

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

        addressIdMetro = memberRepository.save(new Address(null, member, true, DeliveryCharge.METROPOLITAN_AREA)).getId();
        addressIdJeju = memberRepository.save(new Address(null, member, true, DeliveryCharge.JEJU)).getId();

        Coupon fixedCoupon = Coupon.builder()
                .member(member)
                .name("고정할인쿠폰")
                .expireDt(LocalDateTime.now().plusDays(1))
                .couponUsed(false)
                .duplicationAllowed(false)
                .couponType(CouponType.FIXED)
                .minPurchaseAmount(10000)
                .discountAmount(1000)
                .discountRate(0)
                .maxDiscountAmount(0)
                .build();

        Coupon rateCoupon = Coupon.builder()
                .member(member)
                .name("비율할인쿠폰")
                .expireDt(LocalDateTime.now().plusDays(1))
                .couponUsed(false)
                .duplicationAllowed(true)
                .couponType(CouponType.RATE)
                .minPurchaseAmount(10000)
                .discountAmount(0)
                .discountRate(10)
                .maxDiscountAmount(20000)
                .build();

        this.fixedCoupon = couponRepository.save(fixedCoupon);
        this.rateCoupon = couponRepository.save(rateCoupon);
    }

    @DisplayName("별도의 할인, 추가비용 없이 구매 초기화 진행")
    @Test
    public void defaultPayment() {
        //given
        PaymentRequest paymentRequest = new PaymentRequest(orderIdempotencyKey, PaymentMethod.TOSS_PAY, "DEFAULT", new ArrayList<>(), addressIdMetro);

        //when
        PaymentResponse paymentResponse = PaymentStep.결제_생성(accessToken, paymentRequest).jsonPath().getObject("", PaymentResponse.class);

        //then
        int expectedTotalPayAmount = PRODUCT1_PRICE * PRODUCT1_ORDER_QUANTITY + PRODUCT2_PRICE * PRODUCT2_ORDER_QUANTITY + DeliveryCharge.METROPOLITAN_AREA.getAmount();
        assertThat(paymentResponse.getTotalPayAmount()).isEqualTo(expectedTotalPayAmount);

    }

    @DisplayName("추가 배달비 포함 구매 초기화 진행")
    @Test
    public void deliveryFeePayment() {
        //given
        PaymentRequest paymentRequest = new PaymentRequest(orderIdempotencyKey, PaymentMethod.TOSS_PAY, "DEFAULT", new ArrayList<>(), addressIdJeju);

        //when
        PaymentResponse paymentResponse = PaymentStep.결제_생성(accessToken, paymentRequest).jsonPath().getObject("", PaymentResponse.class);

        //then
        int expectedTotalPayAmount = PRODUCT1_PRICE * PRODUCT1_ORDER_QUANTITY + PRODUCT2_PRICE * PRODUCT2_ORDER_QUANTITY + DeliveryCharge.JEJU.getAmount();
        assertThat(paymentResponse.getTotalPayAmount()).isEqualTo(expectedTotalPayAmount);

    }

    @DisplayName("고정액수 할인쿠폰을 적용 구매 초기화 진행")
    @Test
    public void fixedCouponPayment() {
        //given
        PaymentRequest paymentRequest = new PaymentRequest(orderIdempotencyKey, PaymentMethod.TOSS_PAY, "DEFAULT", Arrays.asList(fixedCoupon.getId()), addressIdJeju);

        //when
        PaymentResponse paymentResponse = PaymentStep.결제_생성(accessToken, paymentRequest).jsonPath().getObject("", PaymentResponse.class);
        Payment payment = orderRepository.findPaymentById(paymentResponse.getPaymentId(), memberId).get();

        //then
        int productPriceSum = PRODUCT1_PRICE * PRODUCT1_ORDER_QUANTITY + PRODUCT2_PRICE * PRODUCT2_ORDER_QUANTITY;
        int expectedTotalPayAmount = productPriceSum - fixedCoupon.getDiscountAmount(productPriceSum) + DeliveryCharge.JEJU.getAmount();
        assertThat(paymentResponse.getTotalPayAmount()).isEqualTo(expectedTotalPayAmount);
        assertThat(payment.getTotalDiscountAmount()).isEqualTo(-fixedCoupon.getDiscountAmount(productPriceSum));
    }

    @DisplayName("고정,비율 할인쿠폰을 적용 구매 초기화 진행")
    @Test
    public void rateCouponPayment() {
        //given
        PaymentRequest paymentRequest = new PaymentRequest(orderIdempotencyKey, PaymentMethod.TOSS_PAY, "DEFAULT", Arrays.asList(rateCoupon.getId(), fixedCoupon.getId()), addressIdJeju);

        //when
        PaymentResponse paymentResponse = PaymentStep.결제_생성(accessToken, paymentRequest).jsonPath().getObject("", PaymentResponse.class);
        Payment payment = orderRepository.findPaymentById(paymentResponse.getPaymentId(), memberId).get();

        //then
        int productPriceSum = PRODUCT1_PRICE * PRODUCT1_ORDER_QUANTITY + PRODUCT2_PRICE * PRODUCT2_ORDER_QUANTITY;
        int fixedDiscountedAmount = fixedCoupon.getDiscountAmount(productPriceSum);
        int rateDiscountAmount = rateCoupon.getDiscountAmount(productPriceSum - fixedDiscountedAmount);
        int expectedTotalPayAmount = productPriceSum - fixedDiscountedAmount - rateDiscountAmount + DeliveryCharge.JEJU.getAmount();
        assertThat(paymentResponse.getTotalPayAmount()).isEqualTo(expectedTotalPayAmount);
        assertThat(payment.getTotalDiscountAmount()).isEqualTo(-fixedDiscountedAmount - rateDiscountAmount);

    }

    @DisplayName("네이버 페이 할인 구매 초기화 case")
    @Test
    public void naverPayPayment() {
        //given
        PaymentRequest paymentRequest = new PaymentRequest(orderIdempotencyKey, PaymentMethod.NAVER_PAY, "NAVER_PAY", Arrays.asList(rateCoupon.getId(), fixedCoupon.getId()), addressIdJeju);

        //when
        PaymentResponse paymentResponse = PaymentStep.결제_생성(accessToken, paymentRequest).jsonPath().getObject("", PaymentResponse.class);

        //then
        int productPriceSum = PRODUCT1_PRICE * PRODUCT1_ORDER_QUANTITY + PRODUCT2_PRICE * PRODUCT2_ORDER_QUANTITY;
        int eventDiscountAmount = 2000;
        int expectedTotalPayAmount = productPriceSum - eventDiscountAmount + DeliveryCharge.JEJU.getAmount();
        assertThat(paymentResponse.getTotalPayAmount()).isEqualTo(expectedTotalPayAmount);
    }


}
