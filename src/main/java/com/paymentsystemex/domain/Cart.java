package com.paymentsystemex.domain;

import com.paymentsystemex.domain.member.Member;
import com.paymentsystemex.domain.product.Product;
import com.paymentsystemex.domain.product.ProductOption;
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
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "productOption_id", nullable = false)
    private ProductOption productOption;

    @Column
    private int quantity;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createDate;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updateDate;

    public Cart(Member member, Product product ,ProductOption productOption, int quantity) {
        this.member = member;
        this.product = product;
        this.productOption = productOption;
        this.quantity = quantity;
    }

    public void updateQuantity(int changeAmount){

        if(getQuantity() + changeAmount <=0){
            throw new IllegalArgumentException();
        }

        this.quantity = quantity + changeAmount;
    }
}
