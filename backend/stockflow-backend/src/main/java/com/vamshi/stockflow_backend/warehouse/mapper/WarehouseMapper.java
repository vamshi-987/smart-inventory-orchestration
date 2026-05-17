package com.vamshi.stockflow_backend.warehouse.mapper;

import com.vamshi.stockflow_backend.warehouse.domain.Warehouse;
import com.vamshi.stockflow_backend.warehouse.dto.WarehouseCreateRequest;
import com.vamshi.stockflow_backend.warehouse.dto.WarehouseResponse;
import com.vamshi.stockflow_backend.warehouse.dto.WarehouseUpdateRequest;
import org.springframework.stereotype.Component;

@Component
public class WarehouseMapper {

    public Warehouse toEntity(WarehouseCreateRequest request) {
        return Warehouse.builder()
                .name(request.getName())
                .city(request.getCity())
                .pincode(request.getPincode())
                .address(request.getAddress())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .serviceRadiusKm(request.getServiceRadiusKm())
                .active(true)
                .build();
    }

    public WarehouseResponse toResponse(Warehouse warehouse) {
        return WarehouseResponse.builder()
                .id(warehouse.getId())
                .name(warehouse.getName())
                .city(warehouse.getCity())
                .pincode(warehouse.getPincode())
                .address(warehouse.getAddress())
                .latitude(warehouse.getLatitude())
                .longitude(warehouse.getLongitude())
                .serviceRadiusKm(warehouse.getServiceRadiusKm())
                .active(warehouse.getActive())
                .build();
    }

    public void updateEntity(Warehouse warehouse, WarehouseUpdateRequest request) {
        warehouse.setName(request.getName());
        warehouse.setCity(request.getCity());
        warehouse.setPincode(request.getPincode());
        warehouse.setAddress(request.getAddress());
        warehouse.setLatitude(request.getLatitude());
        warehouse.setLongitude(request.getLongitude());
        warehouse.setServiceRadiusKm(request.getServiceRadiusKm());
        warehouse.setActive(request.getActive());
    }
}