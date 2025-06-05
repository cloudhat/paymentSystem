package com.paymentsystemex.domain.payment.repository;

import com.paymentsystemex.domain.payment.entity.Payment;
import com.paymentsystemex.domain.payment.entity.PaymentStatus;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.paymentsystemex.domain.payment.entity.QPayment.payment;

@RequiredArgsConstructor
@Repository
public class PaymentRepository {

    private final JPAQueryFactory queryFactory;
    private final EntityManager em;

    @Transactional
    public Payment savePayment(Payment payment) {
        em.persist(payment);

        return payment;
    }

    public Optional<Payment> findPaymentById(Long id, Long memberId) {
        Payment result = queryFactory
                .selectFrom(payment)
                .where(payment.id.eq(id)
                        .and(payment.member.id.eq(memberId)))
                .fetchOne();

        return Optional.of(result);
    }

    @Transactional
    public void updatePaymentStatus(Long paymentId, PaymentStatus paymentStatus) {
        queryFactory
                .update(payment)
                .set(payment.paymentStatus, paymentStatus)
                .where(payment.id.eq(paymentId))
                .execute();
    }

    public Optional<Payment> findPaymentByPaykey(String payKey) {
        Payment result = queryFactory
                .selectFrom(payment)
                .where(payment.payKey.eq(payKey))
                .fetchOne();

        return Optional.of(result);
    }
}
