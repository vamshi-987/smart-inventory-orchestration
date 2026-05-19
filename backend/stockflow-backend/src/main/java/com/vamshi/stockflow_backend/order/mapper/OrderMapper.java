package com.vamshi.stockflow_backend.order.mapper;

import com.vamshi.stockflow_backend.order.domain.Order;
import com.vamshi.stockflow_backend.order.dto.OrderItemResponse;
import com.vamshi.stockflow_backend.order.dto.OrderResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderMapper {

    public OrderResponse toResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getItems()
                .stream()
                .map(item -> OrderItemResponse.builder()
                        .productId(item.getProduct().getId())
                        .productName(item.getProduct().getName())
                        .sku(item.getProduct().getSku())
                        .quantity(item.getQuantity())
                        .priceAtOrderTime(item.getPriceAtOrderTime())
                        .lineTotal(item.getLineTotal())
                        .build())
                .toList();

        return OrderResponse.builder()
                .id(order.getId())
                .customerName(order.getCustomerName())
                .deliveryCity(order.getDeliveryCity())
                .deliveryPincode(order.getDeliveryPincode())
                .allocatedWarehouseId(
                        order.getAllocatedWarehouse() != null
                                ? order.getAllocatedWarehouse().getId()
                                : null
                )
                .allocatedWarehouseName(
                        order.getAllocatedWarehouse() != null
                                ? order.getAllocatedWarehouse().getName()
                                : null
                )
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .items(itemResponses)
                .build();
    }
}