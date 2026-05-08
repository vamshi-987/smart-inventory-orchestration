package com.vamshi.stockflow_backend.product.repository;

import com.vamshi.stockflow_backend.product.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
    boolean existsByNameIgnoreCase(String name);
}
