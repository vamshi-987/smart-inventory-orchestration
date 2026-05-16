package com.vamshi.stockflow_backend.product.service;

import com.vamshi.stockflow_backend.category.domain.Category;
import com.vamshi.stockflow_backend.category.dto.CategoryResponse;
import com.vamshi.stockflow_backend.product.domain.Product;
import com.vamshi.stockflow_backend.product.dto.ProductCreateRequest;
import com.vamshi.stockflow_backend.product.dto.ProductResponse;
import com.vamshi.stockflow_backend.product.mapper.ProductMapper;
import com.vamshi.stockflow_backend.category.repository.CategoryRepository;
import com.vamshi.stockflow_backend.product.repository.ProductRepository;
import com.vamshi.stockflow_backend.product.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceImplTest {

    private final ProductRepository productRepository = mock(ProductRepository.class);
    private final CategoryRepository categoryRepository = mock(CategoryRepository.class);
    private final ProductMapper productMapper = new ProductMapper();

    private final ProductServiceImpl productService =
            new ProductServiceImpl(productRepository, categoryRepository, productMapper);

    @Test
    void createProduct_ShouldCreateProduct_WhenSkuIsUnique() {
        Category category = Category.builder()
                .name("Grocery")
                .build();
        category.setId(UUID.randomUUID());
        category.setDeleted(false);

        ProductCreateRequest request = new ProductCreateRequest();
        request.setSku("MILK-001");
        request.setName("Milk");
        request.setDescription("Fresh milk");
        request.setCategoryId(category.getId());
        request.setPrice(BigDecimal.valueOf(60));

        Product product = Product.builder()
                .sku("MILK-001")
                .name("Milk")
                .description("Fresh milk")
                .category(category)
                .price(BigDecimal.valueOf(60))
                .build();
        product.setDeleted(false);

        Product savedProduct = Product.builder()
                .sku("MILK-001")
                .name("Milk")
                .description("Fresh milk")
                .category(category)
                .price(BigDecimal.valueOf(60))
                .build();
        savedProduct.setId(UUID.randomUUID());
        savedProduct.setDeleted(false);


        when(productRepository.existsBySku("MILK-001")).thenReturn(false);
        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
        when(productRepository.save(Mockito.any(Product.class))).thenReturn(savedProduct);

        ProductResponse response = productService.createProduct(request);

        assertNotNull(response);
        assertEquals("MILK-001", response.getSku());
        assertEquals("Milk", response.getName());

        verify(productRepository).save(Mockito.any(Product.class));
    }

    @Test
    void createProduct_ShouldThrowException_WhenSkuAlreadyExists() {
        ProductCreateRequest request = new ProductCreateRequest();
        request.setSku("MILK-001");
        request.setName("Milk");
        request.setDescription("Fresh milk");
        request.setCategoryId(UUID.randomUUID());
        request.setPrice(BigDecimal.valueOf(60));

        when(productRepository.existsBySku("MILK-001")).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> productService.createProduct(request));

        assertTrue(exception.getMessage().contains("Product already exists"));
    }
}
