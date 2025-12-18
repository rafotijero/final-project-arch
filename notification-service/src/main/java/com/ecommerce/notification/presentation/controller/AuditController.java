package com.ecommerce.notification.presentation.controller;

import com.ecommerce.notification.application.dto.AuditLogDTO;
import com.ecommerce.notification.application.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
public class AuditController {

    private final AuditService auditService;

    @GetMapping
    public ResponseEntity<List<AuditLogDTO>> getAllAuditLogs() {
        return ResponseEntity.ok(auditService.getAllAuditLogs());
    }

    @GetMapping("/entity/{entityType}/{entityId}")
    public ResponseEntity<List<AuditLogDTO>> getEntityHistory(
            @PathVariable String entityType,
            @PathVariable Long entityId) {
        return ResponseEntity.ok(auditService.getEntityHistory(entityType, entityId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AuditLogDTO>> getUserActivity(@PathVariable Long userId) {
        return ResponseEntity.ok(auditService.getUserActivity(userId));
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<AuditLogDTO>> getAuditLogsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(auditService.getAuditLogsByDateRange(start, end));
    }
}
