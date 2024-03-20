package com.paymentsystemex.domain.coupon;

import com.paymentsystemex.domain.member.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    Member member;

    @Column
    private int discountPrice;

    @Column(nullable = false)
    private LocalDateTime expireDt;

    @ColumnDefault("true")
    private boolean couponUsed;

    @Column
    private LocalDateTime usedDt;

    @Enumerated(EnumType.STRING)
    private CouponStatus couponStatus;
}
