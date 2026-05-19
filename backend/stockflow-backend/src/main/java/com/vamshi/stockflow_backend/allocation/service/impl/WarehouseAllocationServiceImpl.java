package com.vamshi.stockflow_backend.allocation.service.impl;

import com.vamshi.stockflow_backend.allocation.service.WarehouseAllocationService;
import com.vamshi.stockflow_backend.order.dto.PlaceOrderRequest;
import com.vamshi.stockflow_backend.warehouse.domain.Warehouse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WarehouseAllocationServiceImpl implements WarehouseAllocationService {

    @Override
    public Warehouse allocateWarehouse(PlaceOrderRequest request) {
        throw new UnsupportedOperationException("Allocation logic not implemented yet");
    }
}