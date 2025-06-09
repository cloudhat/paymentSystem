package core.domain.order.repository;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import core.domain.order.dto.OrderHistoryRequest;
import core.domain.order.entity.Orders;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static core.domain.order.entity.QOrders.orders;


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

    private BooleanExpression betweenOrdersCreateAt(OrderHistoryRequest search) {
        return orders.createDate.between(search.getCreateAtStartDate(), search.getCreateAtEndDate());
    }

    private OrderSpecifier[] ordersOrderBy(OrderHistoryRequest search) {
        List<OrderSpecifier> orderSpecifiers = new ArrayList<>();

        Order order = Boolean.TRUE.equals(search.getDescending()) ? Order.DESC : Order.ASC;
        orderSpecifiers.add(new OrderSpecifier(order, orders.id));

        return orderSpecifiers.toArray(new OrderSpecifier[orderSpecifiers.size()]);
    }

}
