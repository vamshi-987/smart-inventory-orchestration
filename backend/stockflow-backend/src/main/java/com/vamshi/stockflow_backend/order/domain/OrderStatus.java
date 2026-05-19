package com.vamshi.stockflow_backend.order.domain;

public enum OrderStatus {
    CREATED,
    ALLOCATED,
    CONFIRMED,
    CANCELLED,
    FAILED,
    OUT_OF_SERVICE_AREA
}