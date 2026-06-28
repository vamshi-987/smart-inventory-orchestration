package com.vamshi.stockflow_backend.order.service;

import com.vamshi.stockflow_backend.order.dto.DeliveryStatusHistoryResponse;
import com.vamshi.stockflow_backend.order.dto.OrderResponse;
import com.vamshi.stockflow_backend.order.dto.OrderStatusHistoryResponse;
import com.vamshi.stockflow_backend.order.dto.PlaceOrderRequest;
import com.vamshi.stockflow_backend.order.dto.UpdateDeliveryStatusRequest;
import com.vamshi.stockflow_backend.order.dto.UpdateOrderStatusRequest;
import com.vamshi.stockflow_backend.order.domain.OrderStatus;
import org.springframework.data.domain.Page;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface OrderService {

    OrderResponse placeOrder(PlaceOrderRequest request);

    OrderResponse cancelOrder(UUID orderId);

    List<OrderResponse> getAllOrders();

    OrderResponse getOrderById(UUID id);

    OrderResponse updateOrderStatus(UUID id, UpdateOrderStatusRequest request);

    OrderResponse updateDeliveryStatus(UUID id, UpdateDeliveryStatusRequest request);

    List<OrderStatusHistoryResponse> getOrderTimeline(UUID id);

    List<DeliveryStatusHistoryResponse> getDeliveryTimeline(UUID id);

    Page<OrderResponse> searchOrders(
            OrderStatus status,
            String customerName,
            Instant fromCreatedAt,
            Instant toCreatedAt,
            int page,
            int size
    );
}