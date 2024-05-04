package com.paymentsystemex.domain.order;

import com.paymentsystemex.domain.member.Member;
import com.paymentsystemex.domain.payment.Payment;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@EntityListeners(AuditingEntityListener.class)
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String idempotencyKey;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @Column
    private String addressDetail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @OneToMany(mappedBy = "orders", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderProduct> orderProducts = new ArrayList<>();

    @OneToMany(mappedBy = "orders", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderCart> orderCarts = new ArrayList<>();

    @OneToMany(mappedBy = "orders", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderPriceHistory> orderPriceHistories = new ArrayList<>();

    @OneToMany(mappedBy = "orders", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Payment> payments = new ArrayList<>();

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createDate;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updateDate;

    public Orders(String idempotencyKey, Member member) {
        this.idempotencyKey = idempotencyKey;
        this.orderStatus = OrderStatus.BEFORE_PAYMENT;
        this.member = member;
    }

    public String getOrderProductSummary() {

        if (orderProducts.isEmpty()) {
            throw new IllegalStateException("Order product list is empty");
        }

        String firstProductName = orderProducts.get(0).getProductName();
        int remainingCount = orderProducts.size() - 1;

        if (remainingCount == 0) {
            return firstProductName;
        } else {
            return firstProductName + " 외 " + remainingCount + "건";
        }
    }

    public void changeStatusToComplete() {
        this.orderStatus = OrderStatus.ORDER_COMPLETE;
    }

    public void changeStatusToCancel() {
        orderProducts.stream()
                .forEach(OrderProduct::changeStatusToCancel);
    }

    public void setAddressDetail(String addressDetail) {
        this.addressDetail = addressDetail;
    }

    public String getPaymentStatus() {
        return payments.get(0).getPaymentStatus().getStatus();
    }

    public Payment getNormalPayment() {

        if(payments.isEmpty()){
            return null;
        }

        return payments.get(0);
    }
}
