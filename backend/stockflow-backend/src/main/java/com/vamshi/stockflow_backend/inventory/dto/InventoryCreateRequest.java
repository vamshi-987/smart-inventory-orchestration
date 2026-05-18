package com.vamshi.stockflow_backend.inventory.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryCreateRequest {

    @NotNull(message = "Warehouse id is required")
    private UUID warehouseId;

    @NotNull(message = "Product id is required")
    private UUID productId;

    @NotNull(message = "Available quantity is required")
    @Min(value = 0, message = "Available quantity cannot be negative")
    private Integer availableQuantity;

    @NotNull(message = "Reserved quantity is required")
    @Min(value = 0, message = "Reserved quantity cannot be negative")
    private Integer reservedQuantity;

    @NotNull(message = "Low stock threshold is required")
    @Min(value = 1, message = "Low stock threshold must be positive")
    private Integer lowStockThreshold;
}