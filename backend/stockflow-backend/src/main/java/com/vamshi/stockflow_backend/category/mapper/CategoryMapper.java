package com.vamshi.stockflow_backend.category.mapper;

import com.vamshi.stockflow_backend.category.domain.Category;
import com.vamshi.stockflow_backend.category.dto.CategoryCreateRequest;
import com.vamshi.stockflow_backend.category.dto.CategoryResponse;
import com.vamshi.stockflow_backend.category.dto.CategoryUpdateRequest;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public Category toEntity(CategoryCreateRequest request) {
        Category category = Category.builder()
                .name(request.getName())
                .build();

        category.setDeleted(false);

        return category;
    }

    public void updateEntity(Category category, CategoryUpdateRequest request) {
        category.setName(request.getName());
        category.setDeleted(request.isDeleted());
    }

    public CategoryResponse toResponse(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.isDeleted()
        );
    }
}