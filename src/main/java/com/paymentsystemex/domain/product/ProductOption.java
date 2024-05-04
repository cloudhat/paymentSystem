package com.paymentsystemex.domain.product;

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

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createDate;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updateDate;

    public ProductOption(Integer version, Long id, Product product, String name, int price, int quantity, LocalDateTime saleStartDt, LocalDateTime saleEndDt) {
        this.version = version;
        this.id = id;
        this.product = product;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.saleStartDt = saleStartDt;
        this.saleEndDt = saleEndDt;
    }

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
