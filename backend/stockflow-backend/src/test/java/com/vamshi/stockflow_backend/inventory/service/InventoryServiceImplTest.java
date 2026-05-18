package com.vamshi.stockflow_backend.inventory.service;

import com.vamshi.stockflow_backend.inventory.domain.Inventory;
import com.vamshi.stockflow_backend.inventory.dto.AddStockRequest;
import com.vamshi.stockflow_backend.inventory.dto.InventoryCreateRequest;
import com.vamshi.stockflow_backend.inventory.dto.InventoryResponse;
import com.vamshi.stockflow_backend.inventory.dto.ReduceStockRequest;
import com.vamshi.stockflow_backend.inventory.mapper.InventoryMapper;
import com.vamshi.stockflow_backend.inventory.repository.InventoryRepository;
import com.vamshi.stockflow_backend.inventory.service.impl.InventoryServiceImpl;
import com.vamshi.stockflow_backend.notification.service.NotificationService;
import com.vamshi.stockflow_backend.product.domain.Product;
import com.vamshi.stockflow_backend.product.repository.ProductRepository;
import com.vamshi.stockflow_backend.warehouse.domain.Warehouse;
import com.vamshi.stockflow_backend.warehouse.repository.WarehouseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceImplTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private WarehouseRepository warehouseRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private InventoryMapper inventoryMapper;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private InventoryServiceImpl inventoryService;

    private UUID warehouseId;
    private UUID productId;
    private UUID inventoryId;

    private Warehouse warehouse;
    private Product product;
    private Inventory inventory;

    @BeforeEach
    void setUp() {
        warehouseId = UUID.randomUUID();
        productId = UUID.randomUUID();
        inventoryId = UUID.randomUUID();

        warehouse = Warehouse.builder()
                .name("Hyderabad Warehouse")
                .city("Hyderabad")
                .pincode("500081")
                .address("Madhapur")
                .latitude(17.4483)
                .longitude(78.3915)
                .serviceRadiusKm(10.0)
                .active(true)
                .build();

        warehouse.setId(warehouseId);
        warehouse.setDeleted(false);

        product = Product.builder()
                .sku("MILK-001")
                .name("Milk")
                .description("Fresh milk")
                .category(null)
                .price(BigDecimal.valueOf(60))
                .build();

        product.setId(productId);
        product.setDeleted(false);

        inventory = Inventory.builder()
                .warehouse(warehouse)
                .product(product)
                .availableQuantity(100)
                .reservedQuantity(0)
                .lowStockThreshold(10)
                .build();

        inventory.setId(inventoryId);
        inventory.setDeleted(false);
    }

    @Test
    void createInventory_ShouldCreateInventory_WhenWarehouseProductCombinationIsUnique() {
        InventoryCreateRequest request = InventoryCreateRequest.builder()
                .warehouseId(warehouseId)
                .productId(productId)
                .availableQuantity(100)
                .reservedQuantity(0)
                .lowStockThreshold(10)
                .build();

        InventoryResponse expectedResponse = InventoryResponse.builder()
                .id(inventoryId)
                .warehouseId(warehouseId)
                .warehouseName("Hyderabad Warehouse")
                .productId(productId)
                .productName("Milk")
                .sku("MILK-001")
                .availableQuantity(100)
                .reservedQuantity(0)
                .lowStockThreshold(10)
                .lowStock(false)
                .version(null)
                .build();

        when(warehouseRepository.findById(warehouseId))
                .thenReturn(Optional.of(warehouse));

        when(productRepository.findById(productId))
                .thenReturn(Optional.of(product));

        when(inventoryRepository.findByWarehouseIdAndProductId(warehouseId, productId))
                .thenReturn(Optional.empty());

        when(inventoryRepository.save(any(Inventory.class)))
                .thenReturn(inventory);

        when(inventoryMapper.toResponse(inventory))
                .thenReturn(expectedResponse);

        InventoryResponse response = inventoryService.createInventory(request);

        assertNotNull(response);
        assertEquals(inventoryId, response.getId());
        assertEquals(warehouseId, response.getWarehouseId());
        assertEquals(productId, response.getProductId());
        assertEquals("Hyderabad Warehouse", response.getWarehouseName());
        assertEquals("Milk", response.getProductName());
        assertEquals(100, response.getAvailableQuantity());
        assertFalse(response.getLowStock());

        verify(warehouseRepository).findById(warehouseId);
        verify(productRepository).findById(productId);
        verify(inventoryRepository).findByWarehouseIdAndProductId(warehouseId, productId);
        verify(inventoryRepository).save(any(Inventory.class));
        verify(inventoryMapper).toResponse(inventory);
    }

    @Test
    void createInventory_ShouldThrowException_WhenDuplicateWarehouseProductInventoryExists() {
        InventoryCreateRequest request = InventoryCreateRequest.builder()
                .warehouseId(warehouseId)
                .productId(productId)
                .availableQuantity(100)
                .reservedQuantity(0)
                .lowStockThreshold(10)
                .build();

        when(warehouseRepository.findById(warehouseId))
                .thenReturn(Optional.of(warehouse));

        when(productRepository.findById(productId))
                .thenReturn(Optional.of(product));

        when(inventoryRepository.findByWarehouseIdAndProductId(warehouseId, productId))
                .thenReturn(Optional.of(inventory));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> inventoryService.createInventory(request)
        );

        assertTrue(exception.getMessage().contains("Inventory already exists"));

        verify(inventoryRepository, never()).save(any(Inventory.class));
        verify(inventoryMapper, never()).toResponse(any(Inventory.class));
    }

    @Test
    void addStock_ShouldIncreaseAvailableQuantity() {
        AddStockRequest request = AddStockRequest.builder()
                .quantity(50)
                .build();

        inventory.setAvailableQuantity(150);

        InventoryResponse expectedResponse = InventoryResponse.builder()
                .id(inventoryId)
                .warehouseId(warehouseId)
                .warehouseName("Hyderabad Warehouse")
                .productId(productId)
                .productName("Milk")
                .sku("MILK-001")
                .availableQuantity(150)
                .reservedQuantity(0)
                .lowStockThreshold(10)
                .lowStock(false)
                .build();

        when(inventoryRepository.findById(inventoryId))
                .thenReturn(Optional.of(inventory));

        when(inventoryRepository.save(inventory))
                .thenReturn(inventory);

        when(inventoryMapper.toResponse(inventory))
                .thenReturn(expectedResponse);

        InventoryResponse response = inventoryService.addStock(inventoryId, request);

        assertNotNull(response);
        assertEquals(150, response.getAvailableQuantity());

        verify(inventoryRepository).findById(inventoryId);
        verify(inventoryRepository).save(inventory);
        verify(inventoryMapper).toResponse(inventory);
    }

    @Test
    void reduceStock_ShouldDecreaseAvailableQuantity() {
        ReduceStockRequest request = ReduceStockRequest.builder()
                .quantity(20)
                .build();

        inventory.setAvailableQuantity(80);

        InventoryResponse expectedResponse = InventoryResponse.builder()
                .id(inventoryId)
                .warehouseId(warehouseId)
                .warehouseName("Hyderabad Warehouse")
                .productId(productId)
                .productName("Milk")
                .sku("MILK-001")
                .availableQuantity(80)
                .reservedQuantity(0)
                .lowStockThreshold(10)
                .lowStock(false)
                .build();

        when(inventoryRepository.findById(inventoryId))
                .thenReturn(Optional.of(inventory));

        when(inventoryRepository.save(inventory))
                .thenReturn(inventory);

        when(inventoryMapper.toResponse(inventory))
                .thenReturn(expectedResponse);

        InventoryResponse response = inventoryService.reduceStock(inventoryId, request);

        assertNotNull(response);
        assertEquals(80, response.getAvailableQuantity());

        verify(inventoryRepository).findById(inventoryId);
        verify(inventoryRepository).save(inventory);
        verify(inventoryMapper).toResponse(inventory);
    }

    @Test
    void reduceStock_ShouldThrowException_WhenStockIsInsufficient() {
        ReduceStockRequest request = ReduceStockRequest.builder()
                .quantity(150)
                .build();

        when(inventoryRepository.findById(inventoryId))
                .thenReturn(Optional.of(inventory));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> inventoryService.reduceStock(inventoryId, request)
        );

        assertTrue(exception.getMessage().contains("Cannot reduce more than available stock"));

        verify(inventoryRepository).findById(inventoryId);
        verify(inventoryRepository, never()).save(any(Inventory.class));
        verify(inventoryMapper, never()).toResponse(any(Inventory.class));
    }

    @Test
    void reduceStock_ShouldCreateLowStockNotification_WhenStockIsBelowThreshold() {
        ReduceStockRequest request = ReduceStockRequest.builder()
                .quantity(95)
                .build();

        inventory.setAvailableQuantity(100);
        inventory.setLowStockThreshold(10);

        InventoryResponse expectedResponse = InventoryResponse.builder()
                .id(inventoryId)
                .warehouseId(warehouseId)
                .warehouseName("Hyderabad Warehouse")
                .productId(productId)
                .productName("Milk")
                .sku("MILK-001")
                .availableQuantity(5)
                .reservedQuantity(0)
                .lowStockThreshold(10)
                .lowStock(true)
                .build();

        when(inventoryRepository.findById(inventoryId))
                .thenReturn(Optional.of(inventory));

        when(inventoryRepository.save(inventory))
                .thenReturn(inventory);

        when(inventoryMapper.toResponse(inventory))
                .thenReturn(expectedResponse);

        InventoryResponse response = inventoryService.reduceStock(inventoryId, request);

        assertNotNull(response);
        assertEquals(5, response.getAvailableQuantity());
        assertTrue(response.getLowStock());

        verify(notificationService).createLowStockNotification(anyString());
        verify(inventoryRepository).save(inventory);
        verify(inventoryMapper).toResponse(inventory);
    }

    @Test
    void getStockByWarehouse_ShouldReturnInventoryList() {
        InventoryResponse expectedResponse = InventoryResponse.builder()
                .id(inventoryId)
                .warehouseId(warehouseId)
                .warehouseName("Hyderabad Warehouse")
                .productId(productId)
                .productName("Milk")
                .sku("MILK-001")
                .availableQuantity(100)
                .reservedQuantity(0)
                .lowStockThreshold(10)
                .lowStock(false)
                .build();

        when(inventoryRepository.findByWarehouseId(warehouseId))
                .thenReturn(List.of(inventory));

        when(inventoryMapper.toResponse(inventory))
                .thenReturn(expectedResponse);

        List<InventoryResponse> response = inventoryService.getStockByWarehouse(warehouseId);

        assertEquals(1, response.size());
        assertEquals(warehouseId, response.get(0).getWarehouseId());
        assertEquals("Hyderabad Warehouse", response.get(0).getWarehouseName());

        verify(inventoryRepository).findByWarehouseId(warehouseId);
        verify(inventoryMapper).toResponse(inventory);
    }

    @Test
    void getStockByProduct_ShouldReturnInventoryList() {
        InventoryResponse expectedResponse = InventoryResponse.builder()
                .id(inventoryId)
                .warehouseId(warehouseId)
                .warehouseName("Hyderabad Warehouse")
                .productId(productId)
                .productName("Milk")
                .sku("MILK-001")
                .availableQuantity(100)
                .reservedQuantity(0)
                .lowStockThreshold(10)
                .lowStock(false)
                .build();

        when(inventoryRepository.findByProductId(productId))
                .thenReturn(List.of(inventory));

        when(inventoryMapper.toResponse(inventory))
                .thenReturn(expectedResponse);

        List<InventoryResponse> response = inventoryService.getStockByProduct(productId);

        assertEquals(1, response.size());
        assertEquals(productId, response.get(0).getProductId());
        assertEquals("Milk", response.get(0).getProductName());

        verify(inventoryRepository).findByProductId(productId);
        verify(inventoryMapper).toResponse(inventory);
    }
}