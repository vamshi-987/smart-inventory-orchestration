package com.vamshi.stockflow_backend.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class SalesByWarehouseResponse {

    private UUID warehouseId;
    private String warehouseName;
    private Long orderCount;
    private BigDecimal totalSales;
}