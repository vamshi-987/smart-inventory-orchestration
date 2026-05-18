package com.vamshi.stockflow_backend.inventory.mapper;

import com.vamshi.stockflow_backend.inventory.domain.Inventory;
import com.vamshi.stockflow_backend.inventory.dto.InventoryResponse;
import org.springframework.stereotype.Component;

@Component
public class InventoryMapper {

    public InventoryResponse toResponse(Inventory inventory) {
        boolean lowStock = inventory.getAvailableQuantity() <= inventory.getLowStockThreshold();

        return InventoryResponse.builder()
                .id(inventory.getId())
                .warehouseId(inventory.getWarehouse().getId())
                .warehouseName(inventory.getWarehouse().getName())
                .productId(inventory.getProduct().getId())
                .productName(inventory.getProduct().getName())
                .sku(inventory.getProduct().getSku())
                .availableQuantity(inventory.getAvailableQuantity())
                .reservedQuantity(inventory.getReservedQuantity())
                .lowStockThreshold(inventory.getLowStockThreshold())
                .lowStock(lowStock)
                .version(inventory.getVersion())
                .build();
    }
}