package com.vamshi.stockflow_backend.order.dto;

import com.vamshi.stockflow_backend.order.domain.OrderStatus;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {

    private UUID id;

    private String customerName;

    private String deliveryCity;

    private String deliveryPincode;

    private UUID allocatedWarehouseId;

    private String allocatedWarehouseName;

    private OrderStatus status;

    private BigDecimal totalAmount;

    private List<OrderItemResponse> items;
}