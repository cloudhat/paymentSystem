package com.paymentsystemex.domain.order;

import com.paymentsystemex.domain.Cart;
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
}
