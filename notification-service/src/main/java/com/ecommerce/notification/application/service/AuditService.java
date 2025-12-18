package com.ecommerce.notification.application.service;

import com.ecommerce.notification.application.dto.AuditLogDTO;
import com.ecommerce.notification.domain.model.AuditLog;
import com.ecommerce.notification.domain.repository.AuditLogRepository;
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
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    @Transactional
    public AuditLog createAuditLog(
            String entityType,
            Long entityId,
            Long userId,
            String username,
            AuditLog.Action action,
            String details,
            String previousState,
            String newState) {

        AuditLog auditLog = AuditLog.builder()
                .entityType(entityType)
                .entityId(entityId)
                .userId(userId)
                .username(username)
                .action(action)
                .details(details)
                .previousState(previousState)
                .newState(newState)
                .build();

        auditLog = auditLogRepository.save(auditLog);
        log.info("Audit log created: {} - {} - {}", entityType, entityId, action);

        return auditLog;
    }

    public List<AuditLogDTO> getEntityHistory(String entityType, Long entityId) {
        return auditLogRepository.findEntityHistory(entityType, entityId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<AuditLogDTO> getUserActivity(Long userId) {
        return auditLogRepository.findUserActivity(userId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<AuditLogDTO> getAuditLogsByDateRange(LocalDateTime start, LocalDateTime end) {
        return auditLogRepository.findByTimestampBetween(start, end)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<AuditLogDTO> getAllAuditLogs() {
        return auditLogRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private AuditLogDTO toDTO(AuditLog auditLog) {
        return AuditLogDTO.builder()
                .id(auditLog.getId())
                .entityType(auditLog.getEntityType())
                .entityId(auditLog.getEntityId())
                .userId(auditLog.getUserId())
                .username(auditLog.getUsername())
                .action(auditLog.getAction().name())
                .details(auditLog.getDetails())
                .previousState(auditLog.getPreviousState())
                .newState(auditLog.getNewState())
                .ipAddress(auditLog.getIpAddress())
                .userAgent(auditLog.getUserAgent())
                .timestamp(auditLog.getTimestamp())
                .build();
    }
}
