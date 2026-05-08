package com.vamshi.stockflow_backend.category.service.impl;

import com.vamshi.stockflow_backend.category.domain.Category;
import com.vamshi.stockflow_backend.category.dto.CategoryCreateRequest;
import com.vamshi.stockflow_backend.category.dto.CategoryResponse;
import com.vamshi.stockflow_backend.category.dto.CategoryUpdateRequest;
import com.vamshi.stockflow_backend.category.mapper.CategoryMapper;
import com.vamshi.stockflow_backend.category.repository.CategoryRepository;
import com.vamshi.stockflow_backend.category.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public CategoryResponse createCategory(CategoryCreateRequest request) {

        if (categoryRepository.existsByNameIgnoreCaseAndDeletedFalse(request.getName())) {
            throw new RuntimeException("Category already exists with name: " + request.getName());
        }

        Category category = categoryMapper.toEntity(request);

        Category savedCategory = categoryRepository.save(category);

        return categoryMapper.toResponse(savedCategory);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(UUID id) {

        Category category = categoryRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));

        return categoryMapper.toResponse(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {

        return categoryRepository.findByDeletedFalse()
                .stream()
                .map(categoryMapper::toResponse)
                .toList();
    }

    @Override
    public CategoryResponse updateCategory(UUID id, CategoryUpdateRequest request) {

        Category category = categoryRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));

        categoryMapper.updateEntity(category, request);

        Category updatedCategory = categoryRepository.save(category);

        return categoryMapper.toResponse(updatedCategory);
    }

    @Override
    public void deleteCategory(UUID id) {

        Category category = categoryRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));

        category.setDeleted(true);

        categoryRepository.save(category);
    }
}
