package com.vamshi.stockflow_backend.allocation.service.impl;

import com.vamshi.stockflow_backend.allocation.service.WarehouseAllocationService;
import com.vamshi.stockflow_backend.inventory.domain.Inventory;
import com.vamshi.stockflow_backend.inventory.repository.InventoryRepository;
import com.vamshi.stockflow_backend.order.dto.OrderItemRequest;
import com.vamshi.stockflow_backend.order.dto.PlaceOrderRequest;
import com.vamshi.stockflow_backend.warehouse.domain.Warehouse;
import com.vamshi.stockflow_backend.warehouse.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WarehouseAllocationServiceImpl implements WarehouseAllocationService {

    private final WarehouseRepository warehouseRepository;
    private final InventoryRepository inventoryRepository;

    @Override
    public Warehouse allocateWarehouse(PlaceOrderRequest request) {
        return findByPincodeWithStock(request)
                .or(() -> findByCityWithStock(request))
                .orElseThrow(() -> new RuntimeException("No warehouse available for this location"));
    }

    private Optional<Warehouse> findByPincodeWithStock(PlaceOrderRequest request) {
        List<Warehouse> warehouses = warehouseRepository
                .findByPincodeAndActiveTrue(request.getDeliveryPincode());

        return findWarehouseWithHighestStock(warehouses, request);
    }

    private Optional<Warehouse> findByCityWithStock(PlaceOrderRequest request) {
        List<Warehouse> warehouses = warehouseRepository
                .findByCityIgnoreCaseAndActiveTrue(request.getDeliveryCity());

        return findWarehouseWithHighestStock(warehouses, request);
    }

    private Optional<Warehouse> findWarehouseWithHighestStock(
            List<Warehouse> warehouses,
            PlaceOrderRequest request
    ) {
        if (warehouses == null || warehouses.isEmpty()) {
            return Optional.empty();
        }

        return warehouses.stream()
                .filter(warehouse -> hasEnoughStockForAllItems(warehouse, request))
                .max(Comparator.comparingInt(warehouse -> calculateTotalAvailableStock(warehouse, request)));
    }

    private boolean hasEnoughStockForAllItems(Warehouse warehouse, PlaceOrderRequest request) {
        for (OrderItemRequest item : request.getItems()) {
            Optional<Inventory> inventoryOptional =
                    inventoryRepository.findByWarehouseIdAndProductId(
                            warehouse.getId(),
                            item.getProductId()
                    );

            if (inventoryOptional.isEmpty()) {
                return false;
            }

            Inventory inventory = inventoryOptional.get();

            if (inventory.getAvailableQuantity() < item.getQuantity()) {
                return false;
            }
        }

        return true;
    }

    private int calculateTotalAvailableStock(Warehouse warehouse, PlaceOrderRequest request) {
        int totalStock = 0;

        for (OrderItemRequest item : request.getItems()) {
            Optional<Inventory> inventoryOptional =
                    inventoryRepository.findByWarehouseIdAndProductId(
                            warehouse.getId(),
                            item.getProductId()
                    );

            totalStock += inventoryOptional
                    .map(Inventory::getAvailableQuantity)
                    .orElse(0);
        }

        return totalStock;
    }
}