package com.vamshi.stockflow_backend.inventory.repository;

import com.vamshi.stockflow_backend.inventory.domain.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InventoryRepository extends JpaRepository<Inventory, UUID> {

    Optional<Inventory> findByWarehouseIdAndProductId(UUID warehouseId, UUID productId);

    List<Inventory> findByProductId(UUID productId);

    List<Inventory> findByWarehouseId(UUID warehouseId);
}