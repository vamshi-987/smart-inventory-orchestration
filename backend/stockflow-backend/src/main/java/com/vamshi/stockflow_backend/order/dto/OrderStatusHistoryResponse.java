package com.vamshi.stockflow_backend.order.dto;

import com.vamshi.stockflow_backend.order.domain.OrderStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
public class OrderStatusHistoryResponse {

    private UUID id;
    private UUID orderId;
    private OrderStatus previousStatus;
    private OrderStatus newStatus;
    private String note;
    private Instant changedAt;
}