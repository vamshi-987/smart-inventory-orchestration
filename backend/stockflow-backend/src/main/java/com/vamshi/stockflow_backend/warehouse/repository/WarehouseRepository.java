package com.vamshi.stockflow_backend.warehouse.repository;

import com.vamshi.stockflow_backend.warehouse.domain.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface WarehouseRepository extends JpaRepository<Warehouse, UUID> {

    List<Warehouse> findByPincodeAndActiveTrue(String pincode);

    List<Warehouse> findByCityIgnoreCaseAndActiveTrue(String city);

    List<Warehouse> findByActiveTrue();
}