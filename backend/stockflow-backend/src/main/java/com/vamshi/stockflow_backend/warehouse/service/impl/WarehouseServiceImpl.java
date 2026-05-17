package com.vamshi.stockflow_backend.warehouse.service.impl;

import com.vamshi.stockflow_backend.warehouse.domain.Warehouse;
import com.vamshi.stockflow_backend.warehouse.dto.WarehouseCreateRequest;
import com.vamshi.stockflow_backend.warehouse.dto.WarehouseResponse;
import com.vamshi.stockflow_backend.warehouse.dto.WarehouseUpdateRequest;
import com.vamshi.stockflow_backend.warehouse.mapper.WarehouseMapper;
import com.vamshi.stockflow_backend.warehouse.repository.WarehouseRepository;
import com.vamshi.stockflow_backend.warehouse.service.WarehouseService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final WarehouseMapper warehouseMapper;

    @Override
    public WarehouseResponse createWarehouse(WarehouseCreateRequest request) {
        Warehouse warehouse = warehouseMapper.toEntity(request);
        Warehouse savedWarehouse = warehouseRepository.save(warehouse);
        return warehouseMapper.toResponse(savedWarehouse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WarehouseResponse> getAllWarehouses() {
        return warehouseRepository.findAll()
                .stream()
                .map(warehouseMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public WarehouseResponse getWarehouseById(UUID id) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Warehouse not found with id: " + id));

        return warehouseMapper.toResponse(warehouse);
    }

    @Override
    public WarehouseResponse updateWarehouse(UUID id, WarehouseUpdateRequest request) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Warehouse not found with id: " + id));

        warehouseMapper.updateEntity(warehouse, request);

        Warehouse updatedWarehouse = warehouseRepository.save(warehouse);

        return warehouseMapper.toResponse(updatedWarehouse);
    }

    @Override
    public void deleteWarehouse(UUID id) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Warehouse not found with id: " + id));

        warehouse.setActive(false);
        warehouseRepository.save(warehouse);
    }
}