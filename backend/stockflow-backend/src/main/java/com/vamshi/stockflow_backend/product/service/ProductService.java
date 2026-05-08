package com.vamshi.stockflow_backend.product.service;

import com.vamshi.stockflow_backend.product.dto.ProductCreateRequest;
import com.vamshi.stockflow_backend.product.dto.ProductResponse;
import com.vamshi.stockflow_backend.product.dto.ProductUpdateRequest;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface ProductService {

    ProductResponse createProduct(ProductCreateRequest request);

    ProductResponse updateProduct(UUID id, ProductUpdateRequest request);

    ProductResponse getProductById(UUID id);

    Page<ProductResponse> getProducts(int page, int size, String search, UUID categoryId);

    void deleteProduct(UUID id);
}
