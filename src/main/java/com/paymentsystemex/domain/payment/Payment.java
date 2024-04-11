package com.paymentsystemex.domain.payment;


import com.paymentsystemex.domain.member.Member;
import com.paymentsystemex.domain.order.Orders;
import com.paymentsystemex.domain.payment.PaymentMethod;
import com.paymentsystemex.domain.payment.PaymentStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private int totalPayAmount;

    @Column
    private int totalDiscountAmount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Column
    private String eventType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Orders orders;

    public Payment(int totalPayAmount, int totalDiscountAmount, PaymentMethod paymentMethod, String eventType, Orders orders, Member member) {
        this.totalPayAmount = totalPayAmount;
        this.totalDiscountAmount = totalDiscountAmount;
        this.paymentStatus = PaymentStatus.BEFORE_PAYMENT;
        this.paymentMethod = paymentMethod;
        this.eventType = eventType;
        this.orders = orders;
        this.member = member;
        this.orders.getPayments().add(this);
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column
    private String transactionId;
}
