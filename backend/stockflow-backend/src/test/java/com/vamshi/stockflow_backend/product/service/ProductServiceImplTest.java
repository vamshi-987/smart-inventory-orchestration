package com.vamshi.stockflow_backend.product.service;

import com.vamshi.stockflow_backend.category.domain.Category;
import com.vamshi.stockflow_backend.category.repository.CategoryRepository;
import com.vamshi.stockflow_backend.product.domain.Product;
import com.vamshi.stockflow_backend.product.dto.ProductCreateRequest;
import com.vamshi.stockflow_backend.product.dto.ProductResponse;
import com.vamshi.stockflow_backend.product.mapper.ProductMapper;
import com.vamshi.stockflow_backend.product.repository.ProductRepository;
import com.vamshi.stockflow_backend.product.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductServiceImpl productService;

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

        Product savedProduct = Product.builder()
                .sku("MILK-001")
                .name("Milk")
                .description("Fresh milk")
                .category(category)
                .price(BigDecimal.valueOf(60))
                .build();

        savedProduct.setId(UUID.randomUUID());
        savedProduct.setDeleted(false);

        ProductResponse expectedResponse =
                new ProductResponse(
                        savedProduct.getId(),
                        "MILK-001",
                        "Milk",
                        "Fresh milk",
                        category.getId(),
                        category.getName(),
                        BigDecimal.valueOf(60),
                        false
                );

        when(productRepository.existsBySku("MILK-001"))
                .thenReturn(false);

        when(categoryRepository.findByIdAndDeletedFalse(category.getId()))
                .thenReturn(Optional.of(category));

        when(productMapper.toEntity(request, category))
                .thenReturn(product);

        when(productRepository.save(product))
                .thenReturn(savedProduct);

        when(productMapper.toResponse(savedProduct))
                .thenReturn(expectedResponse);

        // Act
        ProductResponse response = productService.createProduct(request);

        // Assert
        assertNotNull(response);
        assertEquals("MILK-001", response.getSku());
        assertEquals("Milk", response.getName());
        assertEquals(category.getId(), response.getCategoryId());
        assertEquals("Grocery", response.getCategoryName());

        verify(productRepository).existsBySku("MILK-001");
        verify(categoryRepository).findByIdAndDeletedFalse(category.getId());
        verify(productMapper).toEntity(request, category);
        verify(productRepository).save(product);
        verify(productMapper).toResponse(savedProduct);
    }

    @Test
    void createProduct_ShouldThrowException_WhenSkuAlreadyExists() {

        // Arrange

        ProductCreateRequest request = new ProductCreateRequest();

        request.setSku("MILK-001");
        request.setName("Milk");
        request.setDescription("Fresh milk");
        request.setCategoryId(UUID.randomUUID());
        request.setPrice(BigDecimal.valueOf(60));

        when(productRepository.existsBySku("MILK-001"))
                .thenReturn(true);

        // Act + Assert

        RuntimeException exception =
                assertThrows(RuntimeException.class,
                        () -> productService.createProduct(request));

        assertTrue(exception.getMessage()
                .contains("Product already exists"));

        verify(productRepository, never())
                .save(any(Product.class));
    }
}