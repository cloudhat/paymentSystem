package com.paymentsystemex.domain.coupon;

import com.paymentsystemex.domain.member.Member;
import com.paymentsystemex.domain.order.Orders;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@EntityListeners(AuditingEntityListener.class)
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    Member member;

    @Column
    private String name;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Orders orders;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createDate;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updateDate;

    @Builder
    public Coupon(Member member, String name, LocalDateTime expireDt, boolean couponUsed, boolean duplicationAllowed, CouponType couponType, int minPurchaseAmount, int discountAmount, int discountRate, int maxDiscountAmount) {
        this.member = Objects.requireNonNull(member);
        this.name = Objects.requireNonNull(name);
        this.expireDt = Objects.requireNonNull(expireDt);
        this.couponUsed = Objects.requireNonNull(couponUsed);
        this.duplicationAllowed = Objects.requireNonNull(duplicationAllowed);
        this.couponType = Objects.requireNonNull(couponType);
        this.minPurchaseAmount = Objects.requireNonNull(minPurchaseAmount);
        this.discountAmount = Objects.requireNonNull(discountAmount);
        this.discountRate = Objects.requireNonNull(discountRate);
        this.maxDiscountAmount = Objects.requireNonNull(maxDiscountAmount);
    }

    public int getDiscountAmount(int price) {
        if (CouponType.FIXED.equals(couponType)) {
            return discountAmount;
        } else if (CouponType.RATE.equals(couponType)) {
            int calculatedDiscountAmount = price * discountRate / 100;
            return Math.min(calculatedDiscountAmount, maxDiscountAmount);
        } else {
            throw new IllegalArgumentException();
        }
    }

    public boolean isAvailable() {
        LocalDateTime now = LocalDateTime.now();
        return !couponUsed && expireDt.isAfter(now);
    }

    public void markAsUsed(Orders orders) {
        this.orders = orders;
        this.couponUsed = true;
        this.usedDt = LocalDateTime.now();
    }
}
