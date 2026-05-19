package com.vamshi.stockflow_backend.order.service;

import com.vamshi.stockflow_backend.order.dto.OrderResponse;
import com.vamshi.stockflow_backend.order.dto.PlaceOrderRequest;

public interface OrderService {

    OrderResponse placeOrder(PlaceOrderRequest request);
}