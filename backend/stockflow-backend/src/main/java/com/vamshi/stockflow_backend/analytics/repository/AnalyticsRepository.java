package com.vamshi.stockflow_backend.analytics.repository;

import com.vamshi.stockflow_backend.analytics.dto.PeakHourResponse;
import com.vamshi.stockflow_backend.analytics.dto.SalesByWarehouseResponse;
import com.vamshi.stockflow_backend.order.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.UUID;
import java.util.List;

public interface AnalyticsRepository extends JpaRepository<Order, UUID> {

    @Query("""
            select new com.vamshi.stockflow_backend.analytics.dto.SalesByWarehouseResponse(
                o.allocatedWarehouse.id,
                o.allocatedWarehouse.name,
                count(o),
                coalesce(sum(o.totalAmount), 0)
            )
            from Order o
            where o.allocatedWarehouse is not null
              and (:warehouseId is null or o.allocatedWarehouse.id = :warehouseId)
              and (:fromCreatedAt is null or o.createdAt >= :fromCreatedAt)
              and (:toCreatedAt is null or o.createdAt <= :toCreatedAt)
            group by o.allocatedWarehouse.id, o.allocatedWarehouse.name
            order by coalesce(sum(o.totalAmount), 0) desc
            """)
    List<SalesByWarehouseResponse> getSalesByWarehouse(
            @Param("warehouseId") UUID warehouseId,
            @Param("fromCreatedAt") Instant fromCreatedAt,
            @Param("toCreatedAt") Instant toCreatedAt
    );

    @Query("""
            select new com.vamshi.stockflow_backend.analytics.dto.PeakHourResponse(
                function('hour', o.createdAt),
                count(o),
                coalesce(sum(o.totalAmount), 0)
            )
            from Order o
            where (:warehouseId is null or o.allocatedWarehouse.id = :warehouseId)
              and (:fromCreatedAt is null or o.createdAt >= :fromCreatedAt)
              and (:toCreatedAt is null or o.createdAt <= :toCreatedAt)
            group by function('hour', o.createdAt)
            order by function('hour', o.createdAt)
            """)
    List<PeakHourResponse> getPeakHours(
            @Param("warehouseId") UUID warehouseId,
            @Param("fromCreatedAt") Instant fromCreatedAt,
            @Param("toCreatedAt") Instant toCreatedAt
    );
}