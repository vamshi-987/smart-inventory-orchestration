package com.vamshi.stockflow_backend.inventory.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryResponse {

    private UUID id;

    private UUID warehouseId;
    private String warehouseName;

    private UUID productId;
    private String productName;
    private String sku;

    private Integer availableQuantity;
    private Integer reservedQuantity;
    private Integer lowStockThreshold;

    private Boolean lowStock;

    private Long version;
}