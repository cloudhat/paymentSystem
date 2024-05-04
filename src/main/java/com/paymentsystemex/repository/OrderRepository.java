package com.paymentsystemex.repository;

import com.paymentsystemex.domain.order.Orders;
import com.paymentsystemex.domain.payment.Payment;
import com.paymentsystemex.domain.payment.PaymentStatus;
import com.paymentsystemex.dto.order.OrderHistoryRequest;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.paymentsystemex.domain.order.QOrders.orders;
import static com.paymentsystemex.domain.payment.QPayment.payment;

@RequiredArgsConstructor
@Repository
public class OrderRepository {

    private final JPAQueryFactory queryFactory;
    private final EntityManager em;

    @Transactional
    public Orders saveOrders(Orders orders) {
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

    public Optional<Orders> findOrdersById(Long orderId, Long memberId) {
        Orders result = queryFactory
                .selectFrom(orders)
                .where(orders.id.eq(orderId)
                        .and(orders.member.id.eq(memberId)))
                .fetchOne();

        return Optional.of(result);
    }

    public Page<Orders> findOrdersByMemberId(Long memberId, OrderHistoryRequest search) {
        PageRequest pageRequest = search.getPageRequest();

        JPQLQuery<Orders> countQuery = queryFactory
                .selectFrom(orders)
                .where(orders.member.id.eq(memberId)
                        , betweenOrdersCreateAt(search));

        List<Orders> content = countQuery
                .offset(pageRequest.getOffset())
                .limit(pageRequest.getPageSize())
                .orderBy(ordersOrderBy(search))
                .fetch();


        return PageableExecutionUtils.getPage(content, pageRequest, countQuery::fetchCount);

    }

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

    private BooleanExpression betweenOrdersCreateAt(OrderHistoryRequest search) {
        return orders.createDate.between(search.getCreateAtStartDate(), search.getCreateAtEndDate());
    }

    private OrderSpecifier[] ordersOrderBy(OrderHistoryRequest search) {
        List<OrderSpecifier> orderSpecifiers = new ArrayList<>();

        Order order = search.getDescending() ? Order.DESC : Order.ASC;
        orderSpecifiers.add(new OrderSpecifier(order, orders.id));

        return orderSpecifiers.toArray(new OrderSpecifier[orderSpecifiers.size()]);
    }

}
