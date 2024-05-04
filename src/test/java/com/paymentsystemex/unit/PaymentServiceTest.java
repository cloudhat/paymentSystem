package com.paymentsystemex.unit;

import com.paymentsystemex.domain.member.Member;
import com.paymentsystemex.domain.order.OrderStatus;
import com.paymentsystemex.domain.order.Orders;
import com.paymentsystemex.domain.payment.Payment;
import com.paymentsystemex.domain.payment.PaymentStatus;
import com.paymentsystemex.repository.MemberRepository;
import com.paymentsystemex.repository.OrderRepository;
import com.paymentsystemex.service.payment.PaymentService;
import com.paymentsystemex.utils.AcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class PaymentServiceTest extends AcceptanceTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    PaymentService paymentService;

    private Member member;
    private String idempotencyKey;
    private Payment validPayment;
    private Payment expiredPayment;

    @BeforeEach
    public void setGivenData() throws NoSuchFieldException, IllegalAccessException {
        member = new Member("EMAIL", "PASSWORD", 50);
        memberRepository.save(member);
        Orders orders = new Orders(UUID.randomUUID().toString(), member);
        idempotencyKey = orders.getIdempotencyKey();
        orderRepository.saveOrders(orders);

        validPayment = new Payment(0, 0, null, null, orders, member);
        orderRepository.savePayment(validPayment);

        expiredPayment = new Payment(0, 0, null, null, orders, member);
        Field expireTimeField = Payment.class.getDeclaredField("expireTime");
        expireTimeField.setAccessible(true);
        expireTimeField.set(expiredPayment, LocalDateTime.now().minusMinutes(30));
        orderRepository.savePayment(expiredPayment);
    }

    @DisplayName("정상적인 절차로 결제상태를 변경한다")
    @Test
    public void validTransaction() {
        //when
        paymentService.initTransaction(validPayment.getId(), member.getId(), "payKeyExam");
        orderRepository.updatePaymentStatus(validPayment.getId(), PaymentStatus.FAIL);
        paymentService.initTransaction(validPayment.getId(), member.getId(), "payKeyExam");
        paymentService.completeTransaction(validPayment.getId(), member.getId());

        //then
        Payment payment = orderRepository.findPaymentById(validPayment.getId(), member.getId()).get();
        Orders orders = orderRepository.findOrdersByIdempotencyKey(idempotencyKey, member.getId()).get();
        assertThat(payment.getPaymentStatus()).isEqualTo(PaymentStatus.COMPLETE);
        assertThat(orders.getOrderStatus()).isEqualTo(OrderStatus.ORDER_COMPLETE);
    }

    @DisplayName("유효기간이 지난 후 결제상태를 변경한다")
    @Test
    public void expiredTransaction() {

        //when & then
        assertThatThrownBy(() -> paymentService.initTransaction(expiredPayment.getId(), member.getId(), "payKeyExam"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("이미 transaction이 시작된 payment에 대해 다시 transaction 시도")
    @Test
    public void invalidTransaction1() {
        //when
        paymentService.initTransaction(validPayment.getId(), member.getId(), "payKeyExam");

        //then
        assertThatThrownBy(() -> paymentService.initTransaction(validPayment.getId(), member.getId(), "payKeyExam"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("이미 transaction이 완료된 payment에 대해 다시 transaction 시도")
    @Test
    public void invalidTransaction2() {
        //when
        paymentService.initTransaction(validPayment.getId(), member.getId(), "payKeyExam");
        paymentService.completeTransaction(validPayment.getId(), member.getId());

        //then
        assertThatThrownBy(() -> paymentService.initTransaction(validPayment.getId(), member.getId(), "payKeyExam"))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> paymentService.completeTransaction(validPayment.getId(), member.getId()))
                .isInstanceOf(IllegalArgumentException.class);

    }
}
