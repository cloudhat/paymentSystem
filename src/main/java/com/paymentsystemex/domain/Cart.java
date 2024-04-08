package com.paymentsystemex.domain;

import com.paymentsystemex.domain.member.Member;
import com.paymentsystemex.domain.product.Product;
import com.paymentsystemex.domain.product.ProductOption;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
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
