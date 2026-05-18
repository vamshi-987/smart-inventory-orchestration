package com.vamshi.stockflow_backend.inventory.service;

import com.vamshi.stockflow_backend.inventory.dto.AddStockRequest;
import com.vamshi.stockflow_backend.inventory.dto.InventoryCreateRequest;
import com.vamshi.stockflow_backend.inventory.dto.InventoryResponse;
import com.vamshi.stockflow_backend.inventory.dto.ReduceStockRequest;

import java.util.List;
import java.util.UUID;

public interface InventoryService {

    InventoryResponse createInventory(InventoryCreateRequest request);

    InventoryResponse addStock(UUID inventoryId, AddStockRequest request);

    InventoryResponse reduceStock(UUID inventoryId, ReduceStockRequest request);

    List<InventoryResponse> getStockByWarehouse(UUID warehouseId);

    List<InventoryResponse> getStockByProduct(UUID productId);
}