package com.paymentsystemex.domain.product;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class ProductOption {

    @Version
    private Integer version;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(length = 20, nullable = false)
    private String name;

    @Column
    private int price;

    @Column
    int quantity;

    @Column(nullable = false)
    private LocalDateTime saleStartDt;

    @Column(nullable = false)
    private LocalDateTime saleEndDt;

    public boolean isCurrentlyAvailable() {
        LocalDateTime now = LocalDateTime.now();
        return (saleStartDt == null || saleStartDt.isBefore(now)) && (saleEndDt == null || saleEndDt.isAfter(now));
    }

    public void updateQuantity(int changeAmount) {

        if (getQuantity() + changeAmount < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }

        this.quantity = quantity + changeAmount;
    }
}
