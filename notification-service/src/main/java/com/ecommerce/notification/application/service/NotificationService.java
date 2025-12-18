package com.ecommerce.notification.application.service;

import com.ecommerce.notification.application.dto.NotificationDTO;
import com.ecommerce.notification.domain.model.Notification;
import com.ecommerce.notification.domain.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final EmailService emailService;

    @Transactional
    public Notification createAndSendNotification(
            String recipient,
            String subject,
            String body,
            Notification.NotificationType type,
            Long relatedEntityId,
            String relatedEntityType,
            boolean isHtml) {

        // Create notification record
        Notification notification = Notification.builder()
                .recipient(recipient)
                .subject(subject)
                .body(body)
                .type(type)
                .status(Notification.NotificationStatus.PENDING)
                .relatedEntityId(relatedEntityId)
                .relatedEntityType(relatedEntityType)
                .build();

        notification = notificationRepository.save(notification);

        // Send email
        try {
            if (isHtml) {
                emailService.sendHtmlEmail(recipient, subject, body);
            } else {
                emailService.sendSimpleEmail(recipient, subject, body);
            }

            notification.setStatus(Notification.NotificationStatus.SENT);
            notification.setSentAt(LocalDateTime.now());
            log.info("Notification sent successfully: {}", notification.getId());
        } catch (Exception e) {
            notification.setStatus(Notification.NotificationStatus.FAILED);
            notification.setErrorMessage(e.getMessage());
            log.error("Failed to send notification: {}", notification.getId(), e);
        }

        return notificationRepository.save(notification);
    }

    public List<NotificationDTO> getNotificationsByRecipient(String recipient) {
        return notificationRepository.findByRecipient(recipient)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<NotificationDTO> getNotificationsByEntity(String entityType, Long entityId) {
        return notificationRepository.findByRelatedEntityTypeAndRelatedEntityId(entityType, entityId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<NotificationDTO> getAllNotifications() {
        return notificationRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private NotificationDTO toDTO(Notification notification) {
        return NotificationDTO.builder()
                .id(notification.getId())
                .recipient(notification.getRecipient())
                .subject(notification.getSubject())
                .body(notification.getBody())
                .type(notification.getType().name())
                .status(notification.getStatus().name())
                .errorMessage(notification.getErrorMessage())
                .relatedEntityId(notification.getRelatedEntityId())
                .relatedEntityType(notification.getRelatedEntityType())
                .createdAt(notification.getCreatedAt())
                .sentAt(notification.getSentAt())
                .build();
    }
}
