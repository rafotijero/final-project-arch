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
public class AuditLogDTO {
    private Long id;
    private String entityType;
    private Long entityId;
    private Long userId;
    private String username;
    private String action;
    private String details;
    private String previousState;
    private String newState;
    private String ipAddress;
    private String userAgent;
    private LocalDateTime timestamp;
}
