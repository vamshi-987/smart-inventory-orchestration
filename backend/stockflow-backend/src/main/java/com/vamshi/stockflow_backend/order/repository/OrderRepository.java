package com.vamshi.stockflow_backend.order.repository;

import com.vamshi.stockflow_backend.order.domain.Order;
import com.vamshi.stockflow_backend.order.domain.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {

    @Query("""
	    select o
	    from Order o
	    where (:status is null or o.status = :status)
	      and (:customerName is null or lower(o.customerName) like lower(concat('%', :customerName, '%')))
	      and (:fromCreatedAt is null or o.createdAt >= :fromCreatedAt)
	      and (:toCreatedAt is null or o.createdAt <= :toCreatedAt)
	    """)
    Page<Order> searchOrders(
	    @Param("status") OrderStatus status,
	    @Param("customerName") String customerName,
	    @Param("fromCreatedAt") Instant fromCreatedAt,
	    @Param("toCreatedAt") Instant toCreatedAt,
	    Pageable pageable
    );
}