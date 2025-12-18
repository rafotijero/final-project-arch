package com.ecommerce.notification.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs", indexes = {
        @Index(name = "idx_entity_type_id", columnList = "entityType,entityId"),
        @Index(name = "idx_user_id", columnList = "userId"),
        @Index(name = "idx_timestamp", columnList = "timestamp")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String entityType;

    @Column(nullable = false)
    private Long entityId;

    private Long userId;

    private String username;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Action action;

    @Column(columnDefinition = "TEXT")
    private String details;

    @Column(columnDefinition = "TEXT")
    private String previousState;

    @Column(columnDefinition = "TEXT")
    private String newState;

    private String ipAddress;

    private String userAgent;

    @Column(nullable = false, updatable = false)
    private LocalDateTime timestamp;

    @PrePersist
    protected void onCreate() {
        timestamp = LocalDateTime.now();
    }

    public enum Action {
        CREATE,
        UPDATE,
        DELETE,
        READ,
        LOGIN,
        LOGOUT,
        PAYMENT,
        SHIPMENT,
        OTHER
    }
}
