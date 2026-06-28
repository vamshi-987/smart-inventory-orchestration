package com.vamshi.stockflow_backend.inventory.controller;

import com.vamshi.stockflow_backend.inventory.dto.AddStockRequest;
import com.vamshi.stockflow_backend.inventory.dto.InventoryCreateRequest;
import com.vamshi.stockflow_backend.inventory.dto.InventoryResponse;
import com.vamshi.stockflow_backend.inventory.dto.ReduceStockRequest;
import com.vamshi.stockflow_backend.inventory.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','WAREHOUSE_MANAGER')")
    public ResponseEntity<InventoryResponse> createInventory(
            @Valid @RequestBody InventoryCreateRequest request
    ) {
        InventoryResponse response = inventoryService.createInventory(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}/add-stock")
    @PreAuthorize("hasAnyRole('ADMIN','WAREHOUSE_MANAGER','WAREHOUSE_STAFF')")
    public ResponseEntity<InventoryResponse> addStock(
            @PathVariable UUID id,
            @Valid @RequestBody AddStockRequest request
    ) {
        return ResponseEntity.ok(inventoryService.addStock(id, request));
    }

    @PutMapping("/{id}/reduce-stock")
    @PreAuthorize("hasAnyRole('ADMIN','WAREHOUSE_MANAGER','WAREHOUSE_STAFF')")
    public ResponseEntity<InventoryResponse> reduceStock(
            @PathVariable UUID id,
            @Valid @RequestBody ReduceStockRequest request
    ) {
        return ResponseEntity.ok(inventoryService.reduceStock(id, request));
    }

    @GetMapping("/warehouse/{warehouseId}")
    @PreAuthorize("hasAnyRole('ADMIN','WAREHOUSE_MANAGER','WAREHOUSE_STAFF')")
    public ResponseEntity<List<InventoryResponse>> getStockByWarehouse(
            @PathVariable UUID warehouseId
    ) {
        return ResponseEntity.ok(inventoryService.getStockByWarehouse(warehouseId));
    }

    @GetMapping("/product/{productId}")
    @PreAuthorize("hasAnyRole('ADMIN','WAREHOUSE_MANAGER','WAREHOUSE_STAFF')")
    public ResponseEntity<List<InventoryResponse>> getStockByProduct(
            @PathVariable UUID productId
    ) {
        return ResponseEntity.ok(inventoryService.getStockByProduct(productId));
    }
}