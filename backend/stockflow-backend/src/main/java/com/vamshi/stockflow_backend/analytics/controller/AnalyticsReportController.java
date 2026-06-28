package com.vamshi.stockflow_backend.analytics.controller;

import com.vamshi.stockflow_backend.analytics.service.AnalyticsReportService;
import com.vamshi.stockflow_backend.analytics.service.AnalyticsScopeResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/api/analytics/reports")
@RequiredArgsConstructor
public class AnalyticsReportController {

    private static final MediaType TEXT_CSV = new MediaType("text", "csv");

    private final AnalyticsReportService analyticsReportService;
    private final AnalyticsScopeResolver analyticsScopeResolver;

    @GetMapping("/sales-by-warehouse.csv")
    @PreAuthorize("hasAnyRole('ADMIN','WAREHOUSE_MANAGER')")
    public ResponseEntity<byte[]> salesByWarehouseCsv(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            Instant fromCreatedAt,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            Instant toCreatedAt
    ) {
        UUID warehouseId = analyticsScopeResolver.resolveWarehouseFilter();
        byte[] body = analyticsReportService.generateSalesByWarehouseCsv(warehouseId, fromCreatedAt, toCreatedAt);
        return download(body, TEXT_CSV, "sales-by-warehouse.csv");
    }

    @GetMapping("/sales-by-warehouse.pdf")
    @PreAuthorize("hasAnyRole('ADMIN','WAREHOUSE_MANAGER')")
    public ResponseEntity<byte[]> salesByWarehousePdf(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            Instant fromCreatedAt,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            Instant toCreatedAt
    ) {
        UUID warehouseId = analyticsScopeResolver.resolveWarehouseFilter();
        byte[] body = analyticsReportService.generateSalesByWarehousePdf(warehouseId, fromCreatedAt, toCreatedAt);
        return download(body, MediaType.APPLICATION_PDF, "sales-by-warehouse.pdf");
    }

    @GetMapping("/peak-hours.csv")
    @PreAuthorize("hasAnyRole('ADMIN','WAREHOUSE_MANAGER')")
    public ResponseEntity<byte[]> peakHoursCsv(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            Instant fromCreatedAt,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            Instant toCreatedAt
    ) {
        UUID warehouseId = analyticsScopeResolver.resolveWarehouseFilter();
        byte[] body = analyticsReportService.generatePeakHoursCsv(warehouseId, fromCreatedAt, toCreatedAt);
        return download(body, TEXT_CSV, "peak-hours.csv");
    }

    @GetMapping("/peak-hours.pdf")
    @PreAuthorize("hasAnyRole('ADMIN','WAREHOUSE_MANAGER')")
    public ResponseEntity<byte[]> peakHoursPdf(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            Instant fromCreatedAt,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            Instant toCreatedAt
    ) {
        UUID warehouseId = analyticsScopeResolver.resolveWarehouseFilter();
        byte[] body = analyticsReportService.generatePeakHoursPdf(warehouseId, fromCreatedAt, toCreatedAt);
        return download(body, MediaType.APPLICATION_PDF, "peak-hours.pdf");
    }

    private ResponseEntity<byte[]> download(byte[] body, MediaType contentType, String filename) {
        return ResponseEntity.ok()
                .contentType(contentType)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(body);
    }
}
