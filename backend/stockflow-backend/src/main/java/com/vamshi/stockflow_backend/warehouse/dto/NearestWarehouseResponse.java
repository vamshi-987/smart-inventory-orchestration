package com.vamshi.stockflow_backend.warehouse.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NearestWarehouseResponse {

    private UUID id;

    private String name;

    private String city;

    private String pincode;

    private String address;

    private Double latitude;

    private Double longitude;

    private Double serviceRadiusKm;

    private Double distanceKm;
}