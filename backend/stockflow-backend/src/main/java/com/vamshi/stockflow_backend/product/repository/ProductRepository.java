package com.vamshi.stockflow_backend.product.repository;

import com.vamshi.stockflow_backend.product.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {

    boolean existsBySku(String sku);

    Optional<Product> findBySku(String sku);

    Optional<Product> findByIdAndDeletedFalse(UUID id);

    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Page<Product> findByDeletedFalse(Pageable pageable);

    Page<Product> findByNameContainingIgnoreCaseAndCategoryIdAndDeletedFalse(
            String name,
            Long categoryId,
            Pageable pageable
    );

    Page<Product> findByNameContainingIgnoreCaseAndDeletedFalse(
            String name,
            Pageable pageable
    );

    Page<Product> findByCategoryIdAndDeletedFalse(
            Long categoryId,
            Pageable pageable
    );
}
