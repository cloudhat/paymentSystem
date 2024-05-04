package com.paymentsystemex.service;

import com.paymentsystemex.domain.order.OrderProduct;
import com.paymentsystemex.domain.product.ProductOption;
import com.paymentsystemex.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateQuantity(List<OrderProduct> orderProductList) {

        List<Long> productOptionIdList = orderProductList.stream()
                .map(orderProduct -> orderProduct.getProductOption().getId())
                .toList();
        List<ProductOption> productOptionList = productRepository.findProductOptionListById(productOptionIdList);

        for (OrderProduct orderProduct : orderProductList) {
            for (ProductOption productOption : productOptionList) {
                if (orderProduct.getProductOption().getId().equals(productOption.getId())) {
                    productOption.updateQuantity(-orderProduct.getQuantity());
                }
            }
        }

        productRepository.bulkUpdateQuantityWithOptimisticLock(productOptionList);

    }
}
