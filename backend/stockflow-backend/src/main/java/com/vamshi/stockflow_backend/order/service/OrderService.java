package com.vamshi.stockflow_backend.order.service;

import com.vamshi.stockflow_backend.order.dto.OrderResponse;
import com.vamshi.stockflow_backend.order.dto.PlaceOrderRequest;
import com.vamshi.stockflow_backend.order.dto.UpdateOrderStatusRequest;

import java.util.List;
import java.util.UUID;

public interface OrderService {

    OrderResponse placeOrder(PlaceOrderRequest request);

    OrderResponse cancelOrder(UUID orderId);

    List<OrderResponse> getAllOrders();

    OrderResponse getOrderById(UUID id);

    OrderResponse updateOrderStatus(UUID id, UpdateOrderStatusRequest request);
}