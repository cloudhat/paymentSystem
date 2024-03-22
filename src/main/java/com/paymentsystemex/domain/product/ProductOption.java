package com.paymentsystemex.domain.product;

import com.paymentsystemex.domain.member.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ProductOption {

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

    @Column(nullable = false)
    private LocalDateTime saleStartDt;

    @Column(nullable = false)
    private LocalDateTime saleEndDt;

    public boolean isCurrentlyAvailable() {
        LocalDateTime now = LocalDateTime.now();
        return (saleStartDt == null || saleStartDt.isBefore(now)) && (saleEndDt == null || saleEndDt.isAfter(now));
    }
}
