package com.paymentsystemex.repository;

import com.paymentsystemex.domain.product.Product;
import com.paymentsystemex.domain.product.ProductOption;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.paymentsystemex.domain.product.QProductOption.productOption;

@RequiredArgsConstructor
@Repository
public class ProductRepository {

    private final JPAQueryFactory queryFactory;
    private final EntityManager em;

    public Product getReferenceById(Long id) {
        return em.getReference(Product.class, id);
    }

    @Transactional
    public Product saveProduct(Product product) {
        em.persist(product);

        return product;
    }

    @Transactional
    public ProductOption saveProductOption(ProductOption productOption) {
        em.persist(productOption);

        return productOption;
    }

    public Optional<ProductOption> findProductOptionById(Long id) {
        return Optional.ofNullable(queryFactory
                .selectFrom(productOption)
                .where(productOption.id.eq(id))
                .fetchOne());
    }

    public List<ProductOption> findProductOptionListById(List<Long> idList) {
        return queryFactory
                .selectFrom(productOption)
                .where(productOption.id.in(idList))
                .setLockMode(LockModeType.OPTIMISTIC)
                .fetch();
    }

    @Transactional
    public void bulkUpdateQuantityWithOptimisticLock(List<ProductOption> productOptionList) {

        for (ProductOption updatedProductOption : productOptionList) {
            queryFactory
                    .update(productOption)
                    .set(productOption.quantity, updatedProductOption.getQuantity())
                    .set(productOption.version, updatedProductOption.getVersion() + 1)
                    .where(
                            productOption.id.eq(updatedProductOption.getId()),
                            productOption.version.eq(updatedProductOption.getVersion())
                    )
                    .execute();
        }

        em.clear();
    }
}
