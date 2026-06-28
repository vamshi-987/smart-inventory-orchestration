package com.vamshi.stockflow_backend.order.controller;

import com.vamshi.stockflow_backend.order.dto.DeliveryStatusHistoryResponse;
import com.vamshi.stockflow_backend.order.dto.OrderResponse;
import com.vamshi.stockflow_backend.order.dto.OrderStatusHistoryResponse;
import com.vamshi.stockflow_backend.order.dto.PlaceOrderRequest;
import com.vamshi.stockflow_backend.order.dto.UpdateDeliveryStatusRequest;
import com.vamshi.stockflow_backend.order.dto.UpdateOrderStatusRequest;
import com.vamshi.stockflow_backend.order.domain.OrderStatus;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.data.domain.Page;
import com.vamshi.stockflow_backend.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','WAREHOUSE_MANAGER','WAREHOUSE_STAFF','CUSTOMER')")
    public ResponseEntity<OrderResponse> placeOrder(
            @Valid @RequestBody PlaceOrderRequest request
    ) {
        OrderResponse response = orderService.placeOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN','WAREHOUSE_MANAGER','WAREHOUSE_STAFF','CUSTOMER')")
    public ResponseEntity<OrderResponse> cancelOrder(@PathVariable UUID id) {
        return ResponseEntity.ok(orderService.cancelOrder(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','WAREHOUSE_MANAGER','WAREHOUSE_STAFF')")
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

        @GetMapping("/search")
        @PreAuthorize("hasAnyRole('ADMIN','WAREHOUSE_MANAGER','WAREHOUSE_STAFF')")
        public ResponseEntity<Page<OrderResponse>> searchOrders(
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) String customerName,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            Instant fromCreatedAt,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            Instant toCreatedAt,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
        ) {
        return ResponseEntity.ok(
            orderService.searchOrders(
                status,
                customerName,
                fromCreatedAt,
                toCreatedAt,
                page,
                size
            )
        );
        }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','WAREHOUSE_MANAGER','WAREHOUSE_STAFF','CUSTOMER')")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable UUID id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN','WAREHOUSE_MANAGER','WAREHOUSE_STAFF')")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateOrderStatusRequest request
    ) {
        return ResponseEntity.ok(orderService.updateOrderStatus(id, request));
    }

    @PutMapping("/{id}/delivery-status")
    @PreAuthorize("hasAnyRole('ADMIN','WAREHOUSE_MANAGER','WAREHOUSE_STAFF')")
    public ResponseEntity<OrderResponse> updateDeliveryStatus(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateDeliveryStatusRequest request
    ) {
        return ResponseEntity.ok(orderService.updateDeliveryStatus(id, request));
    }

    @GetMapping("/{id}/timeline")
    @PreAuthorize("hasAnyRole('ADMIN','WAREHOUSE_MANAGER','WAREHOUSE_STAFF','CUSTOMER')")
    public ResponseEntity<List<OrderStatusHistoryResponse>> getOrderTimeline(@PathVariable UUID id) {
        return ResponseEntity.ok(orderService.getOrderTimeline(id));
    }

    @GetMapping("/{id}/delivery-timeline")
    @PreAuthorize("hasAnyRole('ADMIN','WAREHOUSE_MANAGER','WAREHOUSE_STAFF','CUSTOMER')")
    public ResponseEntity<List<DeliveryStatusHistoryResponse>> getDeliveryTimeline(@PathVariable UUID id) {
        return ResponseEntity.ok(orderService.getDeliveryTimeline(id));
    }
}