package com.vamshi.stockflow_backend.order.domain;

import com.vamshi.stockflow_backend.common.domain.BaseEntity;
import com.vamshi.stockflow_backend.warehouse.domain.Warehouse;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order extends BaseEntity {

    @Column(nullable = false)
    private String customerName;

    @Column(nullable = false)
    private String deliveryCity;

    @Column(nullable = false)
    private String deliveryPincode;

    private Double deliveryLatitude;

    private Double deliveryLongitude;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "allocated_warehouse_id")
    private Warehouse allocatedWarehouse;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(nullable = false)
    private BigDecimal totalAmount;

    @OneToMany(
            mappedBy = "order",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();
}