package com.vamshi.stockflow_backend.category.service;

import com.vamshi.stockflow_backend.category.dto.CategoryCreateRequest;
import com.vamshi.stockflow_backend.category.dto.CategoryResponse;
import com.vamshi.stockflow_backend.category.dto.CategoryUpdateRequest;

import java.util.List;
import java.util.UUID;

public interface CategoryService {

    CategoryResponse createCategory(CategoryCreateRequest request);

    CategoryResponse getCategoryById(UUID id);

    List<CategoryResponse> getAllCategories();

    CategoryResponse updateCategory(UUID id, CategoryUpdateRequest request);

    void deleteCategory(UUID id);
}