package com.paymentsystemex.domain.order;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class OrderPriceHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Orders orders;

    @Enumerated(EnumType.STRING)
    private PriceType priceType;

    @Column
    private String reason;

    @Column
    private int amount;

    public OrderPriceHistory(Orders orders, PriceType priceType, String reason, int amount) {
        this.orders = orders;
        this.priceType = priceType;
        this.reason = reason;
        this.amount = amount;
        orders.getOrderPriceHistories().add(this);
    }
}
