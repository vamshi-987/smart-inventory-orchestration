package com.vamshi.stockflow_backend.allocation.service;

import com.vamshi.stockflow_backend.order.dto.PlaceOrderRequest;
import com.vamshi.stockflow_backend.warehouse.domain.Warehouse;

public interface WarehouseAllocationService {

    Warehouse allocateWarehouse(PlaceOrderRequest request);
}