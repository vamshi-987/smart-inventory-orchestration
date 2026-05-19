package com.vamshi.stockflow_backend.order.repository;

import com.vamshi.stockflow_backend.order.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
}