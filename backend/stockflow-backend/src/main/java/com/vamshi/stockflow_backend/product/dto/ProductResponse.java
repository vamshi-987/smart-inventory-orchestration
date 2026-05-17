package com.vamshi.stockflow_backend.product.dto;

import jdk.jfr.Name;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponse {

    private UUID id;

    private String sku;

    private String name;

    private String description;

    private UUID categoryId;

    private String categoryName;

    private BigDecimal price;

    private boolean deleted;
}