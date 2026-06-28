package com.vamshi.stockflow_backend.order.dto;

import com.vamshi.stockflow_backend.order.domain.DeliveryStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateDeliveryStatusRequest {

    @NotNull(message = "Delivery status is required")
    private DeliveryStatus status;
}