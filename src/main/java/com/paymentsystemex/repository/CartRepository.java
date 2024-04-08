package com.paymentsystemex.repository;

import com.paymentsystemex.domain.Cart;
import com.paymentsystemex.domain.member.Member;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.paymentsystemex.domain.QCart.cart;
import static com.paymentsystemex.domain.product.QProductOption.productOption;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class CartRepository {
    private final JPAQueryFactory queryFactory;
    private EntityManager em;

    public Cart save(Cart cart){
        em.persist(cart);

        return cart;
    }

    public List<Cart> findByMemberId(Long memberId){

        return queryFactory
                .selectFrom(cart)
                .join(cart.productOption,productOption).fetchJoin()
                .where(cart.member.id.eq(memberId))
                .fetch();
    }
}
