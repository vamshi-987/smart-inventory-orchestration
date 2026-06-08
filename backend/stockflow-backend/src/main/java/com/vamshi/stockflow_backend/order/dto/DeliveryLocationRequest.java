package com.vamshi.stockflow_backend.order.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryLocationRequest {

    @NotBlank(message = "Formatted address is required")
    private String formattedAddress;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "Pincode is required")
    private String pincode;

    @NotNull(message = "Latitude is required")
    private Double latitude;

    @NotNull(message = "Longitude is required")
    private Double longitude;
}