package com.vamshi.stockflow_backend.order.domain;

import com.vamshi.stockflow_backend.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "delivery_status_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryStatusHistory extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryStatus previousStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryStatus newStatus;

    @Column(length = 500)
    private String note;
}
