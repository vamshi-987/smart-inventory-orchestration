package com.vamshi.stockflow_backend.warehouse.service;

import com.vamshi.stockflow_backend.warehouse.dto.NearestWarehouseResponse;
import com.vamshi.stockflow_backend.warehouse.dto.WarehouseCreateRequest;
import com.vamshi.stockflow_backend.warehouse.dto.WarehouseResponse;
import com.vamshi.stockflow_backend.warehouse.dto.WarehouseUpdateRequest;

import java.util.List;
import java.util.UUID;

public interface WarehouseService {

    WarehouseResponse createWarehouse(WarehouseCreateRequest request);

    List<WarehouseResponse> getAllWarehouses();

    WarehouseResponse getWarehouseById(UUID id);

    WarehouseResponse updateWarehouse(UUID id, WarehouseUpdateRequest request);

    void deleteWarehouse(UUID id);

    List<NearestWarehouseResponse> findNearestWarehouses(Double lat, Double lng);
}