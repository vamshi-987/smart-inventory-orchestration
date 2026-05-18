package com.vamshi.stockflow_backend.inventory.domain;

import com.vamshi.stockflow_backend.common.domain.BaseEntity;
import com.vamshi.stockflow_backend.product.domain.Product;
import com.vamshi.stockflow_backend.warehouse.domain.Warehouse;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "inventory",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"warehouse_id", "product_id"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inventory extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer availableQuantity;

    @Column(nullable = false)
    private Integer reservedQuantity;

    @Column(nullable = false)
    private Integer lowStockThreshold;

    @Version
    private Long version;
}