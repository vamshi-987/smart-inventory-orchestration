package com.vamshi.stockflow_backend.notification.service.impl;

import com.vamshi.stockflow_backend.notification.service.NotificationService;
import org.springframework.stereotype.Service;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Override
    public void createLowStockNotification(String message) {
        System.out.println("LOW STOCK ALERT: " + message);
    }
}