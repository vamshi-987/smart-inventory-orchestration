package com.vamshi.stockflow_backend.order.dto;

import com.vamshi.stockflow_backend.order.domain.DeliveryStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
public class DeliveryStatusHistoryResponse {

    private UUID id;
    private UUID orderId;
    private DeliveryStatus previousStatus;
    private DeliveryStatus newStatus;
    private String note;
    private Instant changedAt;
}
