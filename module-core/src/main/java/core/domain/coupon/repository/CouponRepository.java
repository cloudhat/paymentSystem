package core.domain.coupon.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import core.domain.coupon.entity.Coupon;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static core.domain.coupon.entity.QCoupon.coupon;

@RequiredArgsConstructor
@Repository
public class CouponRepository {

    private final JPAQueryFactory queryFactory;
    private final EntityManager em;

    @Transactional
    public Coupon save(Coupon coupon){
        em.persist(coupon);
        return coupon;
    }

    public List<Coupon> findByIdInAndMemberId(List<Long> couponIds, Long memberId) {

        return queryFactory
                .selectFrom(coupon)
                .where(coupon.id.in(couponIds)
                        .and(coupon.member.id.eq(memberId)))
                .fetch();
    }

    @Transactional
    public void bulkRollbackAsUnused(Long orderId, Long memberId) {
        queryFactory
                .update(coupon)
                .setNull(coupon.orders)
                .set(coupon.couponUsed, false)
                .setNull(coupon.usedDt)
                .where(coupon.orders.id.eq(orderId)
                        .and(coupon.member.id.eq(memberId)))
                .execute();


    }
}
