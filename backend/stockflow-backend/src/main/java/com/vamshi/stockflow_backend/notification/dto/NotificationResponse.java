package com.vamshi.stockflow_backend.notification.dto;

import com.vamshi.stockflow_backend.notification.domain.NotificationType;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponse {

    private UUID id;
    private NotificationType type;
    private String message;
    private Boolean readStatus;
}