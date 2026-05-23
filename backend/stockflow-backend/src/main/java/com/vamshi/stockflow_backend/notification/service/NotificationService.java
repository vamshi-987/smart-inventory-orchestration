package com.vamshi.stockflow_backend.notification.service;

import com.vamshi.stockflow_backend.notification.domain.NotificationType;
import com.vamshi.stockflow_backend.notification.dto.NotificationResponse;

import java.util.List;
import java.util.UUID;

public interface NotificationService {

    void createNotification(NotificationType type, String message);

    void createLowStockNotification(String message);

    List<NotificationResponse> getAllNotifications();

    NotificationResponse markAsRead(UUID id);
}