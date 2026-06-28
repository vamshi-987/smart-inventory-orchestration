package com.vamshi.stockflow_backend.order.service.impl;

import com.vamshi.stockflow_backend.allocation.service.WarehouseAllocationService;
import com.vamshi.stockflow_backend.inventory.domain.Inventory;
import com.vamshi.stockflow_backend.inventory.exception.StockConflictException;
import com.vamshi.stockflow_backend.inventory.repository.InventoryRepository;
import com.vamshi.stockflow_backend.notification.domain.NotificationType;
import com.vamshi.stockflow_backend.notification.service.NotificationService;
import com.vamshi.stockflow_backend.order.domain.DeliveryStatus;
import com.vamshi.stockflow_backend.order.domain.DeliveryStatusHistory;
import com.vamshi.stockflow_backend.order.domain.Order;
import com.vamshi.stockflow_backend.order.domain.OrderItem;
import com.vamshi.stockflow_backend.order.domain.OrderStatusHistory;
import com.vamshi.stockflow_backend.order.domain.OrderStatus;
import com.vamshi.stockflow_backend.order.dto.DeliveryStatusHistoryResponse;
import com.vamshi.stockflow_backend.order.dto.OrderStatusHistoryResponse;
import com.vamshi.stockflow_backend.order.dto.OrderItemRequest;
import com.vamshi.stockflow_backend.order.dto.OrderResponse;
import com.vamshi.stockflow_backend.order.dto.PlaceOrderRequest;
import com.vamshi.stockflow_backend.order.dto.UpdateDeliveryStatusRequest;
import com.vamshi.stockflow_backend.order.dto.UpdateOrderStatusRequest;
import com.vamshi.stockflow_backend.order.mapper.OrderMapper;
import com.vamshi.stockflow_backend.order.repository.DeliveryStatusHistoryRepository;
import com.vamshi.stockflow_backend.order.repository.OrderRepository;
import com.vamshi.stockflow_backend.order.repository.OrderStatusHistoryRepository;
import com.vamshi.stockflow_backend.order.service.OrderService;
import com.vamshi.stockflow_backend.product.domain.Product;
import com.vamshi.stockflow_backend.product.repository.ProductRepository;
import com.vamshi.stockflow_backend.warehouse.domain.Warehouse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final WarehouseAllocationService warehouseAllocationService;
    private final OrderMapper orderMapper;
    private final NotificationService notificationService;
        private final OrderStatusHistoryRepository orderStatusHistoryRepository;
        private final DeliveryStatusHistoryRepository deliveryStatusHistoryRepository;

    @Override
    public OrderResponse placeOrder(PlaceOrderRequest request) {
        try {
            Warehouse allocatedWarehouse = warehouseAllocationService.allocateWarehouse(request);

            Order order = Order.builder()
                    .customerName(request.getCustomerName())
                    .deliveryAddress(request.getDeliveryAddress())
                    .deliveryCity(request.getDeliveryCity())
                    .deliveryPincode(request.getDeliveryPincode())
                    .deliveryLatitude(request.getDeliveryLatitude())
                    .deliveryLongitude(request.getDeliveryLongitude())
                    .allocatedWarehouse(allocatedWarehouse)
                    .status(OrderStatus.ALLOCATED)
                    .deliveryStatus(DeliveryStatus.PENDING)
                    .totalAmount(BigDecimal.ZERO)
                    .build();

            BigDecimal totalAmount = BigDecimal.ZERO;

            for (OrderItemRequest itemRequest : request.getItems()) {
                Product product = productRepository.findById(itemRequest.getProductId())
                        .orElseThrow(() -> new EntityNotFoundException(
                                "Product not found with id: " + itemRequest.getProductId()
                        ));

                Inventory inventory = inventoryRepository
                        .findByWarehouseIdAndProductIdForUpdate(
                                allocatedWarehouse.getId(),
                                product.getId()
                        )
                        .orElseThrow(() -> new EntityNotFoundException(
                                "Inventory not found for product in allocated warehouse"
                        ));

                if (inventory.getAvailableQuantity() < itemRequest.getQuantity()) {
                    throw new IllegalArgumentException("Insufficient stock for product: " + product.getName());
                }

                inventory.setAvailableQuantity(
                        inventory.getAvailableQuantity() - itemRequest.getQuantity()
                );

                inventoryRepository.save(inventory);

                BigDecimal lineTotal = product.getPrice()
                        .multiply(BigDecimal.valueOf(itemRequest.getQuantity()));

                OrderItem orderItem = OrderItem.builder()
                        .order(order)
                        .product(product)
                        .quantity(itemRequest.getQuantity())
                        .priceAtOrderTime(product.getPrice())
                        .lineTotal(lineTotal)
                        .build();

                order.getItems().add(orderItem);
                totalAmount = totalAmount.add(lineTotal);
            }

            order.setTotalAmount(totalAmount);
            order.setStatus(OrderStatus.CONFIRMED);

            Order savedOrder = orderRepository.save(order);
            recordStatusChange(savedOrder, OrderStatus.CREATED, OrderStatus.CONFIRMED, "Warehouse allocated and order confirmed");

            notificationService.createNotification(
                    NotificationType.ORDER_PLACED,
                    "Order placed successfully for customer: "
                            + savedOrder.getCustomerName()
                            + ", order id: "
                            + savedOrder.getId()
            );

            return orderMapper.toResponse(savedOrder);

        } catch (ObjectOptimisticLockingFailureException e) {
            throw new StockConflictException("Stock was updated by another order. Please retry.");
        }
    }

    @Override
    @Transactional
    public OrderResponse cancelOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + orderId));

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalArgumentException("Order is already cancelled");
        }

        if (order.getStatus() == OrderStatus.FAILED ||
                order.getStatus() == OrderStatus.OUT_OF_SERVICE_AREA) {
            throw new IllegalArgumentException("This order cannot be cancelled");
        }

                OrderStatus previousStatus = order.getStatus();
        Warehouse warehouse = order.getAllocatedWarehouse();

        for (OrderItem item : order.getItems()) {
            Inventory inventory = inventoryRepository
                    .findByWarehouseIdAndProductIdForUpdate(
                            warehouse.getId(),
                            item.getProduct().getId()
                    )
                    .orElseThrow(() -> new EntityNotFoundException("Inventory not found"));

            inventory.setAvailableQuantity(
                    inventory.getAvailableQuantity() + item.getQuantity()
            );

            inventoryRepository.save(inventory);
        }

        order.setStatus(OrderStatus.CANCELLED);

        Order savedOrder = orderRepository.save(order);
        recordStatusChange(savedOrder, previousStatus, OrderStatus.CANCELLED, "Order cancelled and inventory restored");

        notificationService.createNotification(
                NotificationType.ORDER_CANCELLED,
                "Order cancelled successfully. Order id: " + savedOrder.getId()
        );

        return orderMapper.toResponse(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(UUID id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + id));

        return orderMapper.toResponse(order);
    }

    @Override
    @Transactional
    public OrderResponse updateOrderStatus(UUID id, UpdateOrderStatusRequest request) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + id));

        OrderStatus previousStatus = order.getStatus();
        order.setStatus(request.getStatus());

        Order savedOrder = orderRepository.save(order);
        recordStatusChange(savedOrder, previousStatus, request.getStatus(), "Order status updated");

        return orderMapper.toResponse(savedOrder);
    }

        @Override
        @Transactional
        public OrderResponse updateDeliveryStatus(UUID id, UpdateDeliveryStatusRequest request) {
                Order order = orderRepository.findById(id)
                                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + id));

                DeliveryStatus previousStatus = order.getDeliveryStatus();
                order.setDeliveryStatus(request.getStatus());

                Order savedOrder = orderRepository.save(order);
                recordDeliveryStatusChange(
                                savedOrder,
                                previousStatus,
                                request.getStatus(),
                                "Delivery status changed from " + previousStatus + " to " + request.getStatus()
                );

                return orderMapper.toResponse(savedOrder);
        }

    @Override
    @Transactional(readOnly = true)
    public List<DeliveryStatusHistoryResponse> getDeliveryTimeline(UUID id) {
        orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + id));

        return deliveryStatusHistoryRepository.findByOrderIdOrderByCreatedAtAsc(id)
                .stream()
                .map(this::toDeliveryStatusHistoryResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderStatusHistoryResponse> getOrderTimeline(UUID id) {
        orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + id));

        return orderStatusHistoryRepository.findByOrderIdOrderByCreatedAtAsc(id)
                .stream()
                .map(this::toStatusHistoryResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<OrderResponse> searchOrders(
            OrderStatus status,
            String customerName,
            Instant fromCreatedAt,
            Instant toCreatedAt,
            int page,
            int size) {

        return orderRepository.searchOrders(
                        status,
                        customerName,
                        fromCreatedAt,
                        toCreatedAt,
                        org.springframework.data.domain.PageRequest.of(
                                page,
                                size,
                                org.springframework.data.domain.Sort.by("createdAt").descending()
                        )
                )
                .map(orderMapper::toResponse);
    }

    private void recordStatusChange(Order order, OrderStatus previousStatus, OrderStatus newStatus, String note) {
        orderStatusHistoryRepository.save(
                OrderStatusHistory.builder()
                        .order(order)
                        .previousStatus(previousStatus)
                        .newStatus(newStatus)
                        .note(note)
                        .build()
        );
    }

    private void recordDeliveryStatusChange(Order order, DeliveryStatus previousStatus, DeliveryStatus newStatus, String note) {
        deliveryStatusHistoryRepository.save(
                DeliveryStatusHistory.builder()
                        .order(order)
                        .previousStatus(previousStatus)
                        .newStatus(newStatus)
                        .note(note)
                        .build()
        );
    }

    private DeliveryStatusHistoryResponse toDeliveryStatusHistoryResponse(DeliveryStatusHistory history) {
        return DeliveryStatusHistoryResponse.builder()
                .id(history.getId())
                .orderId(history.getOrder().getId())
                .previousStatus(history.getPreviousStatus())
                .newStatus(history.getNewStatus())
                .note(history.getNote())
                .changedAt(history.getCreatedAt())
                .build();
    }

    private OrderStatusHistoryResponse toStatusHistoryResponse(OrderStatusHistory history) {
        return OrderStatusHistoryResponse.builder()
                .id(history.getId())
                .orderId(history.getOrder().getId())
                .previousStatus(history.getPreviousStatus())
                .newStatus(history.getNewStatus())
                .note(history.getNote())
                .changedAt(history.getCreatedAt())
                .build();
    }
}