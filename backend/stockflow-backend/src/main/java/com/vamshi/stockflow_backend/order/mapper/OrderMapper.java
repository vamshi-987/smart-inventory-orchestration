package com.vamshi.stockflow_backend.order.mapper;

import com.vamshi.stockflow_backend.order.domain.Order;
import com.vamshi.stockflow_backend.order.domain.OrderItem;
import com.vamshi.stockflow_backend.order.dto.OrderItemResponse;
import com.vamshi.stockflow_backend.order.dto.OrderResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderMapper {

    public OrderResponse toResponse(Order order) {
        if (order == null) {
            return null;
        }

        List<OrderItemResponse> itemResponses = order.getItems()
                .stream()
                .map(this::toItemResponse)
                .toList();

        return OrderResponse.builder()
                .id(order.getId())
                .customerName(order.getCustomerName())
                .deliveryAddress(order.getDeliveryAddress())
                .deliveryCity(order.getDeliveryCity())
                .deliveryPincode(order.getDeliveryPincode())
                .deliveryLatitude(order.getDeliveryLatitude())
                .deliveryLongitude(order.getDeliveryLongitude())
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

    private OrderItemResponse toItemResponse(OrderItem item) {
        return OrderItemResponse.builder()
                .productId(item.getProduct().getId())
                .productName(item.getProduct().getName())
                .quantity(item.getQuantity())
                .priceAtOrderTime(item.getPriceAtOrderTime())
                .lineTotal(item.getLineTotal())
                .build();
    }
}