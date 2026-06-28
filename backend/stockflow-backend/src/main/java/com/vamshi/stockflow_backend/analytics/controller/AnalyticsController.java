package com.vamshi.stockflow_backend.analytics.controller;

import com.vamshi.stockflow_backend.analytics.dto.PeakHourResponse;
import com.vamshi.stockflow_backend.analytics.dto.SalesByWarehouseResponse;
import com.vamshi.stockflow_backend.analytics.service.AnalyticsScopeResolver;
import com.vamshi.stockflow_backend.analytics.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;
    private final AnalyticsScopeResolver analyticsScopeResolver;

    @GetMapping("/sales-by-warehouse")
    @PreAuthorize("hasAnyRole('ADMIN','WAREHOUSE_MANAGER')")
    public ResponseEntity<List<SalesByWarehouseResponse>> getSalesByWarehouse(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            Instant fromCreatedAt,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            Instant toCreatedAt
    ) {
        UUID warehouseId = analyticsScopeResolver.resolveWarehouseFilter();
        return ResponseEntity.ok(analyticsService.getSalesByWarehouse(warehouseId, fromCreatedAt, toCreatedAt));
    }

    @GetMapping("/peak-hours")
    @PreAuthorize("hasAnyRole('ADMIN','WAREHOUSE_MANAGER')")
    public ResponseEntity<List<PeakHourResponse>> getPeakHours(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            Instant fromCreatedAt,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            Instant toCreatedAt
    ) {
        UUID warehouseId = analyticsScopeResolver.resolveWarehouseFilter();
        return ResponseEntity.ok(analyticsService.getPeakHours(warehouseId, fromCreatedAt, toCreatedAt));
    }
}