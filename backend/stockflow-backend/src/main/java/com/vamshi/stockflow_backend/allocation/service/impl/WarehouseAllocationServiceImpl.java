package com.vamshi.stockflow_backend.allocation.service.impl;

import com.vamshi.stockflow_backend.allocation.service.WarehouseAllocationService;
import com.vamshi.stockflow_backend.common.util.LocationUtils;
import com.vamshi.stockflow_backend.inventory.domain.Inventory;
import com.vamshi.stockflow_backend.inventory.repository.InventoryRepository;
import com.vamshi.stockflow_backend.order.dto.OrderItemRequest;
import com.vamshi.stockflow_backend.order.dto.PlaceOrderRequest;
import com.vamshi.stockflow_backend.order.exception.OutOfServiceAreaException;
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
        Optional<Warehouse> pincodeWarehouse = findByPincodeWithStock(request);

        if (pincodeWarehouse.isPresent()) {
            return pincodeWarehouse.get();
        }

        Optional<Warehouse> cityWarehouse = findByCityWithStock(request);

        if (cityWarehouse.isPresent()) {
            return cityWarehouse.get();
        }

        Optional<Warehouse> nearestWarehouse = findNearestWithStockWithinRadius(request);

        if (nearestWarehouse.isPresent()) {
            return nearestWarehouse.get();
        }

        throw new OutOfServiceAreaException("No warehouse available for this location");
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
                // evaluateAvailableStock fetches each warehouse's inventory once and
                // returns -1 when any item is short, avoiding a second pass over the DB.
                .map(warehouse -> new WarehouseStock(
                        warehouse,
                        evaluateAvailableStock(warehouse, request)))
                .filter(candidate -> candidate.totalStock() >= 0)
                .max(Comparator.comparingInt(WarehouseStock::totalStock))
                .map(WarehouseStock::warehouse);
    }

    private boolean hasEnoughStockForAllItems(Warehouse warehouse, PlaceOrderRequest request) {
        return evaluateAvailableStock(warehouse, request) >= 0;
    }

    /**
     * Returns the total available stock across all requested items for the given
     * warehouse, or -1 if any item is missing or has insufficient quantity.
     * Each inventory row is fetched exactly once.
     */
    private int evaluateAvailableStock(Warehouse warehouse, PlaceOrderRequest request) {
        int totalStock = 0;

        for (OrderItemRequest item : request.getItems()) {
            Optional<Inventory> inventoryOptional =
                    inventoryRepository.findByWarehouseIdAndProductId(
                            warehouse.getId(),
                            item.getProductId()
                    );

            if (inventoryOptional.isEmpty()) {
                return -1;
            }

            Inventory inventory = inventoryOptional.get();

            if (inventory.getAvailableQuantity() < item.getQuantity()) {
                return -1;
            }

            totalStock += inventory.getAvailableQuantity();
        }

        return totalStock;
    }

    private record WarehouseStock(Warehouse warehouse, int totalStock) {
    }

    private Optional<Warehouse> findNearestWithStockWithinRadius(PlaceOrderRequest request) {
        if (request.getDeliveryLatitude() == null || request.getDeliveryLongitude() == null) {
            return Optional.empty();
        }

        List<Warehouse> activeWarehouses = warehouseRepository.findByActiveTrue();

        return activeWarehouses.stream()
                .filter(warehouse -> warehouse.getLatitude() != null && warehouse.getLongitude() != null)
                .filter(warehouse -> hasEnoughStockForAllItems(warehouse, request))
                .filter(warehouse -> {
                    double distance = LocationUtils.calculateDistanceKm(
                            request.getDeliveryLatitude(),
                            request.getDeliveryLongitude(),
                            warehouse.getLatitude(),
                            warehouse.getLongitude()
                    );

                    return distance <= warehouse.getServiceRadiusKm();
                })
                .min(Comparator.comparingDouble(warehouse ->
                        LocationUtils.calculateDistanceKm(
                                request.getDeliveryLatitude(),
                                request.getDeliveryLongitude(),
                                warehouse.getLatitude(),
                                warehouse.getLongitude()
                        )
                ));
    }
}