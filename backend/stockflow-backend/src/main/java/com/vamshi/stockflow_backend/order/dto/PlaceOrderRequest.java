package com.vamshi.stockflow_backend.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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

    @Valid
    @NotNull(message = "Delivery location is required")
    private DeliveryLocationRequest deliveryLocation;

    @NotEmpty(message = "Order items are required")
    @Valid
    private List<OrderItemRequest> items;

    public String getDeliveryAddress() {
        return deliveryLocation != null ? deliveryLocation.getFormattedAddress() : null;
    }

    public String getDeliveryCity() {
        return deliveryLocation != null ? deliveryLocation.getCity() : null;
    }

    public String getDeliveryPincode() {
        return deliveryLocation != null ? deliveryLocation.getPincode() : null;
    }

    public Double getDeliveryLatitude() {
        return deliveryLocation != null ? deliveryLocation.getLatitude() : null;
    }

    public Double getDeliveryLongitude() {
        return deliveryLocation != null ? deliveryLocation.getLongitude() : null;
    }


}