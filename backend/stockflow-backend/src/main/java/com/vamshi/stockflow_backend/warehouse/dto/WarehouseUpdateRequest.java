package com.vamshi.stockflow_backend.warehouse.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WarehouseUpdateRequest {

    @NotBlank(message = "Warehouse name is required")
    private String name;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "Pincode is required")
    private String pincode;

    @NotBlank(message = "Address is required")
    private String address;

    private Double latitude;

    private Double longitude;

    @NotNull(message = "Service radius is required")
    private Double serviceRadiusKm;

    @NotNull(message = "Active status is required")
    private Boolean active;
}
