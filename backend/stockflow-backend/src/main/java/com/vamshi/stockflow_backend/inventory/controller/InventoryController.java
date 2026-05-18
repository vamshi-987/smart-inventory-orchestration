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
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping
    public ResponseEntity<InventoryResponse> createInventory(
            @Valid @RequestBody InventoryCreateRequest request
    ) {
        InventoryResponse response = inventoryService.createInventory(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}/add-stock")
    public ResponseEntity<InventoryResponse> addStock(
            @PathVariable UUID id,
            @Valid @RequestBody AddStockRequest request
    ) {
        return ResponseEntity.ok(inventoryService.addStock(id, request));
    }

    @PutMapping("/{id}/reduce-stock")
    public ResponseEntity<InventoryResponse> reduceStock(
            @PathVariable UUID id,
            @Valid @RequestBody ReduceStockRequest request
    ) {
        return ResponseEntity.ok(inventoryService.reduceStock(id, request));
    }

    @GetMapping("/warehouse/{warehouseId}")
    public ResponseEntity<List<InventoryResponse>> getStockByWarehouse(
            @PathVariable UUID warehouseId
    ) {
        return ResponseEntity.ok(inventoryService.getStockByWarehouse(warehouseId));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<InventoryResponse>> getStockByProduct(
            @PathVariable UUID productId
    ) {
        return ResponseEntity.ok(inventoryService.getStockByProduct(productId));
    }
}