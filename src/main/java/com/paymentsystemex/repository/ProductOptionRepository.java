package com.paymentsystemex.repository;

import com.paymentsystemex.domain.product.ProductOption;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductOptionRepository extends JpaRepository<ProductOption , Long> {
}
