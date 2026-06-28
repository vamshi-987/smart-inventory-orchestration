package com.vamshi.stockflow_backend.analytics.service.impl;

import com.vamshi.stockflow_backend.analytics.dto.PeakHourResponse;
import com.vamshi.stockflow_backend.analytics.dto.SalesByWarehouseResponse;
import com.vamshi.stockflow_backend.analytics.repository.AnalyticsRepository;
import com.vamshi.stockflow_backend.analytics.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService {

    private final AnalyticsRepository analyticsRepository;

    @Override
    public List<SalesByWarehouseResponse> getSalesByWarehouse(UUID warehouseId, Instant fromCreatedAt, Instant toCreatedAt) {
        return analyticsRepository.getSalesByWarehouse(warehouseId, fromCreatedAt, toCreatedAt);
    }

    @Override
    public List<PeakHourResponse> getPeakHours(UUID warehouseId, Instant fromCreatedAt, Instant toCreatedAt) {
        return analyticsRepository.getPeakHours(warehouseId, fromCreatedAt, toCreatedAt);
    }
}