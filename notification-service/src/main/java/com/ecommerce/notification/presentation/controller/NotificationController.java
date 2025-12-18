package com.ecommerce.notification.presentation.controller;

import com.ecommerce.notification.application.dto.NotificationDTO;
import com.ecommerce.notification.application.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<NotificationDTO>> getAllNotifications() {
        return ResponseEntity.ok(notificationService.getAllNotifications());
    }

    @GetMapping("/recipient/{email}")
    public ResponseEntity<List<NotificationDTO>> getNotificationsByRecipient(@PathVariable String email) {
        return ResponseEntity.ok(notificationService.getNotificationsByRecipient(email));
    }

    @GetMapping("/entity/{entityType}/{entityId}")
    public ResponseEntity<List<NotificationDTO>> getNotificationsByEntity(
            @PathVariable String entityType,
            @PathVariable Long entityId) {
        return ResponseEntity.ok(notificationService.getNotificationsByEntity(entityType, entityId));
    }
}
