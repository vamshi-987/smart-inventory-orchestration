package com.vamshi.stockflow_backend.order.repository;

import com.vamshi.stockflow_backend.order.domain.DeliveryStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DeliveryStatusHistoryRepository extends JpaRepository<DeliveryStatusHistory, UUID> {

    List<DeliveryStatusHistory> findByOrderIdOrderByCreatedAtAsc(UUID orderId);
}
