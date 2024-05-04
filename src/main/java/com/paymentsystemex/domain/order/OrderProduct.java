package com.paymentsystemex.domain.order;

import com.paymentsystemex.domain.Cart;
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
public class OrderProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String productName;

    @Column
    private String productOptionName;

    @Column
    private int quantity;

    @Column
    private int price;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Orders orders;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "productOption_id", nullable = false)
    private ProductOption productOption;

    @Column
    private DeliveryStatus deliveryStatus;

    @Column
    private String invoiceNum;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createDate;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updateDate;

    public OrderProduct(Long id, String productName, String productOptionName, int quantity, int price, Orders orders, Product product, ProductOption productOption) {
        this.id = id;
        this.productName = productName;
        this.productOptionName = productOptionName;
        this.quantity = quantity;
        this.price = price;
        this.orders = orders;
        this.product = product;
        this.productOption = productOption;
    }

    public OrderProduct(Orders orders, Cart cart) {

        this.productName = cart.getProduct().getName();
        this.productOptionName = cart.getProductOption().getName();
        this.quantity = cart.getQuantity();
        this.price = cart.getProductOption().getPrice();

        this.orders = orders;
        this.product = cart.getProduct();
        this.productOption = cart.getProductOption();
        this.deliveryStatus = DeliveryStatus.BEFORE_STARTED;
        orders.getOrderProducts().add(this);
    }

    public void changeStatusToCancel(){
        if(!deliveryStatus.equals(DeliveryStatus.BEFORE_STARTED)){
            throw new IllegalArgumentException("Delivery has commenced, cancellation of orders is not possible.");
        }
        deliveryStatus = DeliveryStatus.CANCEL;

    }
}
