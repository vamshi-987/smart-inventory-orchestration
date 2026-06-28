package com.vamshi.stockflow_backend.inventory.repository;

import com.vamshi.stockflow_backend.inventory.domain.Inventory;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InventoryRepository extends JpaRepository<Inventory, UUID> {

    Optional<Inventory> findByWarehouseIdAndProductId(UUID warehouseId, UUID productId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM Inventory i WHERE i.warehouse.id = :warehouseId AND i.product.id = :productId")
    Optional<Inventory> findByWarehouseIdAndProductIdForUpdate(
            @Param("warehouseId") UUID warehouseId,
            @Param("productId") UUID productId
    );

    List<Inventory> findByProductId(UUID productId);

    List<Inventory> findByWarehouseId(UUID warehouseId);

    List<Inventory> findByWarehouseIdInAndProductId(
            List<UUID> warehouseIds,
            UUID productId
    );
}