package com.paymentsystemex.repository;

import com.paymentsystemex.domain.coupon.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface CouponRepository extends JpaRepository<Coupon, Long> {

    List<Coupon> findByIdInAndMemberId(List<Long> couponIds, Long memberId);

}
