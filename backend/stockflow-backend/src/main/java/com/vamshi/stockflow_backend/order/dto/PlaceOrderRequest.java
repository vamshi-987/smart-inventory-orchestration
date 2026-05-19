package com.vamshi.stockflow_backend.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlaceOrderRequest {

    @NotBlank(message = "Customer name is required")
    private String customerName;

    @NotBlank(message = "Delivery city is required")
    private String deliveryCity;

    @NotBlank(message = "Delivery pincode is required")
    private String deliveryPincode;

    private Double deliveryLatitude;

    private Double deliveryLongitude;

    @NotEmpty(message = "Order items are required")
    @Valid
    private List<OrderItemRequest> items;
}