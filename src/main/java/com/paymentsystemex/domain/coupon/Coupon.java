package com.paymentsystemex.domain.coupon;

import com.paymentsystemex.domain.member.Member;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    Member member;

    @Column(nullable = false)
    private LocalDateTime expireDt;

    @ColumnDefault("false")
    private boolean couponUsed;

    @Column
    private LocalDateTime usedDt;

    @Column(nullable = false)
    @ColumnDefault("false")
    private boolean duplicationAllowed;

    @Enumerated(EnumType.STRING)
    private CouponType couponType;

    @Column(nullable = false)
    private int minPurchaseAmount;

    @Column(nullable = false)
    private int discountAmount;

    @Column(nullable = false)
    private int discountRate;

    @Column(nullable = false)
    private int maxDiscountAmount;

    public int discount(int price) {
        if (CouponType.FIXED.equals(couponType)) {
            return price - discountAmount;
        } else if (CouponType.RATE.equals(couponType)) {
            int calculatedPrice = price * (100 - discountRate) / 100;
            return calculatedPrice < maxDiscountAmount ? calculatedPrice : maxDiscountAmount;
        } else {
            throw new IllegalArgumentException();
        }
    }

    public boolean isAvailable() {
        LocalDateTime now = LocalDateTime.now();
        return !couponUsed && expireDt.isAfter(now);
    }
}
