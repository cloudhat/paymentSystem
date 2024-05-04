package com.paymentsystemex.domain.payment;


import com.paymentsystemex.domain.member.Member;
import com.paymentsystemex.domain.order.Orders;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@EntityListeners(AuditingEntityListener.class)
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

    @Column
    private LocalDateTime expireTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Orders orders;

    @Column
    private String payKey;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column
    private String transactionId;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createDate;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updateDate;

    public Payment(int totalPayAmount, int totalDiscountAmount, PaymentMethod paymentMethod, String eventType, Orders orders, Member member) {
        this.totalPayAmount = totalPayAmount;
        this.totalDiscountAmount = totalDiscountAmount;
        this.paymentStatus = PaymentStatus.BEFORE_PAYMENT;
        this.paymentMethod = paymentMethod;
        this.eventType = eventType;
        this.orders = orders;
        this.member = member;
        this.expireTime = LocalDateTime.now().plusMinutes(30);
        this.orders.getPayments().add(this);
    }

    public void changeStatusToStart(String payKey) {

        if(expireTime.isBefore(LocalDateTime.now())){
            throw new IllegalArgumentException("Payment expired");
        }

        if (!this.paymentStatus.equals(PaymentStatus.BEFORE_PAYMENT) && !this.paymentStatus.equals(PaymentStatus.FAIL)) {
            throw new IllegalArgumentException("Invalid payment status for starting: " + this.paymentStatus);
        }

        this.paymentStatus = PaymentStatus.STARTED;
        this.payKey = payKey;
    }

    public void changeStatusToComplete() {

        if (!this.paymentStatus.equals(PaymentStatus.STARTED)) {
            throw new IllegalArgumentException("Invalid payment status for complete: " + this.paymentStatus);
        }

        this.paymentStatus = PaymentStatus.COMPLETE;
        this.orders.changeStatusToComplete();
    }

    public void changeStatusToCancel(){
        if(!this.paymentStatus.equals(PaymentStatus.COMPLETE)){
            throw new IllegalArgumentException("Invalid payment status for cancel: " + this.paymentStatus);
        }

        this.paymentStatus = PaymentStatus.CANCEL;
        this.orders.changeStatusToCancel();
    }
}
