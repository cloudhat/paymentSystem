package com.paymentsystemex.repository;

import com.paymentsystemex.domain.order.Orders;
import com.paymentsystemex.domain.payment.Payment;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.paymentsystemex.domain.order.QOrders.orders;
import static com.paymentsystemex.domain.payment.QPayment.payment;

@RequiredArgsConstructor
@Repository
public class OrderRepository {

    private final JPAQueryFactory queryFactory;
    private final EntityManager em;

    public Orders save(Orders orders) {
        em.persist(orders);

        return orders;
    }

    public Optional<Orders> findOrdersByIdempotencyKey(String idempotencyKey, Long memberId) {
        Orders result = queryFactory
                .selectFrom(orders)
                .where(orders.idempotencyKey.eq(idempotencyKey)
                        .and(orders.member.id.eq(memberId)))
                .fetchOne();

        return Optional.of(result);
    }

    public Optional<Payment> findPaymentById(Long id, Long memberId) {
        Payment result = queryFactory
                .selectFrom(payment)
                .where(payment.id.eq(id)
                        .and(payment.member.id.eq(memberId)))
                .fetchOne();

        return Optional.of(result);
    }
}
