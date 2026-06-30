package com.vamshi.stockflow_backend.analytics.controller;

import com.vamshi.stockflow_backend.analytics.dto.PeakHourResponse;
import com.vamshi.stockflow_backend.analytics.dto.SalesByWarehouseResponse;
import com.vamshi.stockflow_backend.analytics.service.AnalyticsScopeResolver;
import com.vamshi.stockflow_backend.analytics.service.AnalyticsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

class AnalyticsControllerTest {

    private MockMvc mockMvc;
    private AnalyticsService analyticsService;
    private AnalyticsScopeResolver analyticsScopeResolver;

    @BeforeEach
    void setUp() {
        analyticsService = mock(AnalyticsService.class);
        analyticsScopeResolver = mock(AnalyticsScopeResolver.class);
        mockMvc = standaloneSetup(
                new AnalyticsController(analyticsService, analyticsScopeResolver)
        ).build();
    }

    @Test
    void getSalesByWarehouse_ShouldReturnGroupedSales() throws Exception {
        when(analyticsScopeResolver.resolveWarehouseFilter()).thenReturn(null);
        when(analyticsService.getSalesByWarehouse(null, null, null)).thenReturn(List.of(
                SalesByWarehouseResponse.builder()
                        .warehouseId(UUID.randomUUID())
                        .warehouseName("Central Warehouse")
                        .orderCount(5L)
                        .totalSales(new BigDecimal("1250.00"))
                        .build()
        ));

        mockMvc.perform(get("/api/analytics/sales-by-warehouse")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].warehouseName").value("Central Warehouse"))
                .andExpect(jsonPath("$[0].orderCount").value(5));
    }

    @Test
    void getSalesByWarehouse_ShouldPassResolvedScopeAndDateRange() throws Exception {
        UUID warehouseId = UUID.randomUUID();
        Instant from = Instant.parse("2026-01-01T00:00:00Z");
        Instant to = Instant.parse("2026-12-31T23:59:59Z");

        when(analyticsScopeResolver.resolveWarehouseFilter()).thenReturn(warehouseId);
        when(analyticsService.getSalesByWarehouse(eq(warehouseId), any(), any()))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/analytics/sales-by-warehouse")
                        .param("fromCreatedAt", from.toString())
                        .param("toCreatedAt", to.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(analyticsService).getSalesByWarehouse(warehouseId, from, to);
    }

    @Test
    void getPeakHours_ShouldReturnHourBuckets() throws Exception {
        when(analyticsScopeResolver.resolveWarehouseFilter()).thenReturn(null);
        when(analyticsService.getPeakHours(null, null, null)).thenReturn(List.of(
                PeakHourResponse.builder()
                        .hour(14)
                        .orderCount(8L)
                        .totalSales(new BigDecimal("2100.00"))
                        .build()
        ));

        mockMvc.perform(get("/api/analytics/peak-hours")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].hour").value(14))
                .andExpect(jsonPath("$[0].orderCount").value(8));
    }

    @Test
    void getPeakHours_ShouldPassResolvedScopeAndDateRange() throws Exception {
        UUID warehouseId = UUID.randomUUID();
        Instant from = Instant.parse("2026-06-01T00:00:00Z");
        Instant to = Instant.parse("2026-06-30T23:59:59Z");

        when(analyticsScopeResolver.resolveWarehouseFilter()).thenReturn(warehouseId);
        when(analyticsService.getPeakHours(eq(warehouseId), any(), any()))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/analytics/peak-hours")
                        .param("fromCreatedAt", from.toString())
                        .param("toCreatedAt", to.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(analyticsService).getPeakHours(warehouseId, from, to);
    }
}
