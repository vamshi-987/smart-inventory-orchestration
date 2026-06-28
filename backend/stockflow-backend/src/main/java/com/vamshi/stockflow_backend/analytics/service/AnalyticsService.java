package com.vamshi.stockflow_backend.analytics.service;

import com.vamshi.stockflow_backend.analytics.dto.PeakHourResponse;
import com.vamshi.stockflow_backend.analytics.dto.SalesByWarehouseResponse;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface AnalyticsService {

    List<SalesByWarehouseResponse> getSalesByWarehouse(UUID warehouseId, Instant fromCreatedAt, Instant toCreatedAt);

    List<PeakHourResponse> getPeakHours(UUID warehouseId, Instant fromCreatedAt, Instant toCreatedAt);
}