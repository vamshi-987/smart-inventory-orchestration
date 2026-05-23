package com.vamshi.stockflow_backend.order.service;

import com.vamshi.stockflow_backend.order.dto.OrderResponse;
import com.vamshi.stockflow_backend.order.dto.PlaceOrderRequest;

import java.util.UUID;

public interface OrderService {

    OrderResponse placeOrder(PlaceOrderRequest request);

    OrderResponse cancelOrder(UUID orderId);
}