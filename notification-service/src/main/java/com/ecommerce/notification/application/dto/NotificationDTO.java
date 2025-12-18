package com.ecommerce.notification.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {
    private Long id;
    private String recipient;
    private String subject;
    private String body;
    private String type;
    private String status;
    private String errorMessage;
    private Long relatedEntityId;
    private String relatedEntityType;
    private LocalDateTime createdAt;
    private LocalDateTime sentAt;
}
