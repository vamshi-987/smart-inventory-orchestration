package com.vamshi.stockflow_backend.order.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemResponse {

    private UUID productId;
    private String productName;
    private String sku;
    private Integer quantity;
    private BigDecimal priceAtOrderTime;
    private BigDecimal lineTotal;
}