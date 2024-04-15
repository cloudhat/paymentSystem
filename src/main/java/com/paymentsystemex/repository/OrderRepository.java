package com.paymentsystemex.repository;

import com.paymentsystemex.domain.order.Orders;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.paymentsystemex.domain.order.QOrders.orders;

import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class OrderRepository {

    private final JPAQueryFactory queryFactory;
    private final EntityManager em;

    public Orders save(Orders orders) {
        em.persist(orders);

        return orders;
    }

    public Optional<Orders> findByIdempotencyKey(String idempotencyKey , Long memberId) {
        Orders result = queryFactory
                .selectFrom(orders)
                .where(orders.idempotencyKey.eq(idempotencyKey)
                        .and(orders.member.id.eq(memberId)))
                .fetchOne();

        return Optional.of(result);
    }
}
