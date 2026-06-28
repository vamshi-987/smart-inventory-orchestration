package com.vamshi.stockflow_backend.analytics.service;

import java.time.Instant;
import java.util.UUID;

public interface AnalyticsReportService {

    byte[] generateSalesByWarehouseCsv(UUID warehouseId, Instant fromCreatedAt, Instant toCreatedAt);

    byte[] generateSalesByWarehousePdf(UUID warehouseId, Instant fromCreatedAt, Instant toCreatedAt);

    byte[] generatePeakHoursCsv(UUID warehouseId, Instant fromCreatedAt, Instant toCreatedAt);

    byte[] generatePeakHoursPdf(UUID warehouseId, Instant fromCreatedAt, Instant toCreatedAt);
}