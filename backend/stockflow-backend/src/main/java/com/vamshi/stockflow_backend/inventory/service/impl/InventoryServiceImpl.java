package com.vamshi.stockflow_backend.inventory.service.impl;

import com.vamshi.stockflow_backend.inventory.domain.Inventory;
import com.vamshi.stockflow_backend.inventory.dto.AddStockRequest;
import com.vamshi.stockflow_backend.inventory.dto.InventoryCreateRequest;
import com.vamshi.stockflow_backend.inventory.dto.InventoryResponse;
import com.vamshi.stockflow_backend.inventory.dto.ReduceStockRequest;
import com.vamshi.stockflow_backend.inventory.mapper.InventoryMapper;
import com.vamshi.stockflow_backend.inventory.repository.InventoryRepository;
import com.vamshi.stockflow_backend.inventory.service.InventoryService;
import com.vamshi.stockflow_backend.product.domain.Product;
import com.vamshi.stockflow_backend.product.repository.ProductRepository;
import com.vamshi.stockflow_backend.warehouse.domain.Warehouse;
import com.vamshi.stockflow_backend.warehouse.repository.WarehouseRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final WarehouseRepository warehouseRepository;
    private final ProductRepository productRepository;
    private final InventoryMapper inventoryMapper;

    @Override
    public InventoryResponse createInventory(InventoryCreateRequest request) {
        validateCreateRequest(request);

        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> new EntityNotFoundException("Warehouse not found with id: " + request.getWarehouseId()));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + request.getProductId()));

        inventoryRepository.findByWarehouseIdAndProductId(
                request.getWarehouseId(),
                request.getProductId()
        ).ifPresent(existingInventory -> {
            throw new IllegalArgumentException("Inventory already exists for this warehouse and product");
        });

        Inventory inventory = Inventory.builder()
                .warehouse(warehouse)
                .product(product)
                .availableQuantity(request.getAvailableQuantity())
                .reservedQuantity(request.getReservedQuantity())
                .lowStockThreshold(request.getLowStockThreshold())
                .build();

        Inventory savedInventory = inventoryRepository.save(inventory);

        return inventoryMapper.toResponse(savedInventory);
    }

    @Override
    public InventoryResponse addStock(UUID inventoryId, AddStockRequest request) {
        if (request.getQuantity() == null || request.getQuantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }

        Inventory inventory = inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new EntityNotFoundException("Inventory not found with id: " + inventoryId));

        inventory.setAvailableQuantity(inventory.getAvailableQuantity() + request.getQuantity());

        Inventory updatedInventory = inventoryRepository.save(inventory);

        return inventoryMapper.toResponse(updatedInventory);
    }

    @Override
    public InventoryResponse reduceStock(UUID inventoryId, ReduceStockRequest request) {
        if (request.getQuantity() == null || request.getQuantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }

        Inventory inventory = inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new EntityNotFoundException("Inventory not found with id: " + inventoryId));

        if (inventory.getAvailableQuantity() < request.getQuantity()) {
            throw new IllegalArgumentException("Cannot reduce more than available stock");
        }

        inventory.setAvailableQuantity(inventory.getAvailableQuantity() - request.getQuantity());

        Inventory updatedInventory = inventoryRepository.save(inventory);

        return inventoryMapper.toResponse(updatedInventory);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryResponse> getStockByWarehouse(UUID warehouseId) {
        return inventoryRepository.findByWarehouseId(warehouseId)
                .stream()
                .map(inventoryMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryResponse> getStockByProduct(UUID productId) {
        return inventoryRepository.findByProductId(productId)
                .stream()
                .map(inventoryMapper::toResponse)
                .toList();
    }

    private void validateCreateRequest(InventoryCreateRequest request) {
        if (request.getAvailableQuantity() == null || request.getAvailableQuantity() < 0) {
            throw new IllegalArgumentException("Available quantity cannot be negative");
        }

        if (request.getReservedQuantity() == null || request.getReservedQuantity() < 0) {
            throw new IllegalArgumentException("Reserved quantity cannot be negative");
        }

        if (request.getLowStockThreshold() == null || request.getLowStockThreshold() <= 0) {
            throw new IllegalArgumentException("Low stock threshold must be positive");
        }
    }
}