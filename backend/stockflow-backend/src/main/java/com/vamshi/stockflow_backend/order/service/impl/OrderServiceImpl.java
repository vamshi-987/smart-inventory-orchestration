package com.vamshi.stockflow_backend.order.service.impl;

import com.vamshi.stockflow_backend.allocation.service.WarehouseAllocationService;
import com.vamshi.stockflow_backend.inventory.domain.Inventory;
import com.vamshi.stockflow_backend.inventory.exception.StockConflictException;
import com.vamshi.stockflow_backend.inventory.repository.InventoryRepository;
import com.vamshi.stockflow_backend.order.domain.Order;
import com.vamshi.stockflow_backend.order.domain.OrderItem;
import com.vamshi.stockflow_backend.order.domain.OrderStatus;
import com.vamshi.stockflow_backend.order.dto.OrderItemRequest;
import com.vamshi.stockflow_backend.order.dto.OrderResponse;
import com.vamshi.stockflow_backend.order.dto.PlaceOrderRequest;
import com.vamshi.stockflow_backend.order.dto.UpdateOrderStatusRequest;
import com.vamshi.stockflow_backend.order.mapper.OrderMapper;
import com.vamshi.stockflow_backend.order.repository.OrderRepository;
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

    @Override
    public OrderResponse placeOrder(PlaceOrderRequest request) {
        try {
            Warehouse allocatedWarehouse = warehouseAllocationService.allocateWarehouse(request);

            Order order = Order.builder()
                    .customerName(request.getCustomerName())
                    .deliveryCity(request.getDeliveryCity())
                    .deliveryPincode(request.getDeliveryPincode())
                    .deliveryLatitude(request.getDeliveryLatitude())
                    .deliveryLongitude(request.getDeliveryLongitude())
                    .allocatedWarehouse(allocatedWarehouse)
                    .status(OrderStatus.ALLOCATED)
                    .totalAmount(BigDecimal.ZERO)
                    .build();

            BigDecimal totalAmount = BigDecimal.ZERO;

            for (OrderItemRequest itemRequest : request.getItems()) {
                Product product = productRepository.findById(itemRequest.getProductId())
                        .orElseThrow(() -> new EntityNotFoundException(
                                "Product not found with id: " + itemRequest.getProductId()
                        ));

                Inventory inventory = inventoryRepository
                        .findByWarehouseIdAndProductId(
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

            try {
                Order savedOrder = orderRepository.save(order);

                return orderMapper.toResponse(savedOrder);
            }catch (ObjectOptimisticLockingFailureException e){
                throw new StockConflictException("Stock was updated by another order. Please retry.");
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
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

        Warehouse warehouse = order.getAllocatedWarehouse();

        for (OrderItem item : order.getItems()) {
            Inventory inventory = inventoryRepository
                    .findByWarehouseIdAndProductId(
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

        order.setStatus(request.getStatus());

        Order savedOrder = orderRepository.save(order);

        return orderMapper.toResponse(savedOrder);
    }
}