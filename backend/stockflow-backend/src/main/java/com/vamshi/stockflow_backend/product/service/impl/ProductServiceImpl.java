package com.vamshi.stockflow_backend.product.service.impl;

import com.vamshi.stockflow_backend.category.domain.Category;
import com.vamshi.stockflow_backend.category.repository.CategoryRepository;
import com.vamshi.stockflow_backend.product.domain.Product;
import com.vamshi.stockflow_backend.product.dto.ProductCreateRequest;
import com.vamshi.stockflow_backend.product.dto.ProductResponse;
import com.vamshi.stockflow_backend.product.dto.ProductUpdateRequest;
import com.vamshi.stockflow_backend.product.mapper.ProductMapper;
import com.vamshi.stockflow_backend.product.repository.ProductRepository;
import com.vamshi.stockflow_backend.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    @Override
    @Transactional
    public ProductResponse createProduct(ProductCreateRequest request) {
        if (productRepository.existsBySku(request.getSku())) {
            throw new RuntimeException("Product already exists with SKU: " + request.getSku());
        }

        Category category = getCategory(request.getCategoryId());

        Product product = productMapper.toEntity(request, category);
        Product savedProduct = productRepository.save(product);

        return productMapper.toResponse(savedProduct);
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(UUID id, ProductUpdateRequest request) {
        Product product = getProduct(id);
        Category category = getCategory(request.getCategoryId());

        productMapper.updateEntity(product, request, category);

        return productMapper.toResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductById(UUID id) {
        Product product = getProduct(id);
        return productMapper.toResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> getProducts(int page, int size, String search, UUID categoryId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        Page<Product> products;

        boolean hasSearch = search != null && !search.isBlank();
        boolean hasCategory = categoryId != null;

        if (hasSearch && hasCategory) {
            products = productRepository.findByNameContainingIgnoreCaseAndCategoryIdAndDeletedFalse(
                    search,
                    categoryId,
                    pageable
            );
        } else if (hasSearch) {
            products = productRepository.findByNameContainingIgnoreCaseAndDeletedFalse(search, pageable);
        } else if (hasCategory) {
            products = productRepository.findByCategoryIdAndDeletedFalse(categoryId, pageable);
        } else {
            products = productRepository.findByDeletedFalse(pageable);
        }

        return products.map(productMapper::toResponse);
    }

    @Override
    @Transactional
    public void deleteProduct(UUID id) {
        Product product = getProduct(id);
        product.setDeleted(true);
    }

    private Product getProduct(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }

    private Category getCategory(UUID id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
    }
}