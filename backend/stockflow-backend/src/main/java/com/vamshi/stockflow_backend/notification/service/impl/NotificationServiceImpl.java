package com.vamshi.stockflow_backend.notification.service.impl;

import com.vamshi.stockflow_backend.notification.domain.Notification;
import com.vamshi.stockflow_backend.notification.domain.NotificationType;
import com.vamshi.stockflow_backend.notification.dto.NotificationResponse;
import com.vamshi.stockflow_backend.notification.repository.NotificationRepository;
import com.vamshi.stockflow_backend.notification.service.NotificationService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    public void createNotification(NotificationType type, String message) {
        Notification notification = Notification.builder()
                .type(type)
                .message(message)
                .readStatus(false)
                .build();

        notificationRepository.save(notification);
    }

    @Override
    public void createLowStockNotification(String message) {
        createNotification(NotificationType.LOW_STOCK, message);
    }

    @Override
    public List<NotificationResponse> getAllNotifications() {
        return notificationRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public NotificationResponse markAsRead(UUID id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Notification not found with id: " + id));

        notification.setReadStatus(true);

        Notification saved = notificationRepository.save(notification);

        return toResponse(saved);
    }

    private NotificationResponse toResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .type(notification.getType())
                .message(notification.getMessage())
                .readStatus(notification.getReadStatus())
                .build();
    }
}