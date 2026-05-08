package com.vamshi.stockflow_backend.category.repository;

import com.vamshi.stockflow_backend.category.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {

    boolean existsByNameIgnoreCaseAndDeletedFalse(String name);

    Optional<Category> findByIdAndDeletedFalse(UUID id);

    List<Category> findByDeletedFalse();
}