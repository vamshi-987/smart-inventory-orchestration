package com.vamshi.stockflow_backend.warehouse.controller;

import com.vamshi.stockflow_backend.warehouse.dto.WarehouseCreateRequest;
import com.vamshi.stockflow_backend.warehouse.dto.WarehouseResponse;
import com.vamshi.stockflow_backend.warehouse.dto.WarehouseUpdateRequest;
import com.vamshi.stockflow_backend.warehouse.service.WarehouseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/warehouses")
@RequiredArgsConstructor
public class WarehouseController {

    private final WarehouseService warehouseService;

    @PostMapping
    public ResponseEntity<WarehouseResponse> createWarehouse(
            @Valid @RequestBody WarehouseCreateRequest request
    ) {
        WarehouseResponse response = warehouseService.createWarehouse(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<WarehouseResponse>> getAllWarehouses() {
        return ResponseEntity.ok(warehouseService.getAllWarehouses());
    }

    @GetMapping("/{id}")
    public ResponseEntity<WarehouseResponse> getWarehouseById(@PathVariable UUID id) {
        return ResponseEntity.ok(warehouseService.getWarehouseById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<WarehouseResponse> updateWarehouse(
            @PathVariable UUID id,
            @Valid @RequestBody WarehouseUpdateRequest request
    ) {
        return ResponseEntity.ok(warehouseService.updateWarehouse(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWarehouse(@PathVariable UUID id) {
        warehouseService.deleteWarehouse(id);
        return ResponseEntity.noContent().build();
    }
}