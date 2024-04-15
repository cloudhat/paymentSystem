package com.paymentsystemex.repository;

import com.paymentsystemex.domain.Cart;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.paymentsystemex.domain.QCart.cart;
import static com.paymentsystemex.domain.product.QProduct.product;
import static com.paymentsystemex.domain.product.QProductOption.productOption;

@RequiredArgsConstructor
@Repository
public class CartRepository {
    private final JPAQueryFactory queryFactory;
    private final EntityManager em;

    public Cart save(Cart cart) {
        em.persist(cart);

        return cart;
    }

    public List<Cart> findByMemberId(Long memberId) {

        return queryFactory
                .selectFrom(cart)
                .join(cart.product, product).fetchJoin()
                .join(cart.productOption, productOption).fetchJoin()
                .where(cart.member.id.eq(memberId))
                .fetch();
    }

    public List<Cart> findByCartIds(List<Long> cartIds, Long memberId) {
        return queryFactory
                .selectFrom(cart)
                .join(cart.product, product).fetchJoin()
                .join(cart.productOption, productOption).fetchJoin()
                .where(cart.id.in(cartIds)
                        .and(cart.member.id.eq(memberId)))
                .fetch();
    }

    @Transactional
    public void delete(Long cartId, Long memberId) {
        long execute = queryFactory
                .delete(cart)
                .where(cart.id.eq(cartId)
                        .and(cart.member.id.eq(memberId)))
                .execute();
    }

}
