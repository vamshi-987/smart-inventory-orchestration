package com.vamshi.stockflow_backend.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
@AllArgsConstructor
public class PeakHourResponse {

    private Integer hour;
    private Long orderCount;
    private BigDecimal totalSales;
}