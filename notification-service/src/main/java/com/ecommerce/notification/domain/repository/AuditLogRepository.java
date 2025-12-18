package com.ecommerce.notification.domain.repository;

import com.ecommerce.notification.domain.model.AuditLog;
import com.ecommerce.notification.domain.model.AuditLog.Action;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    List<AuditLog> findByEntityTypeAndEntityId(String entityType, Long entityId);

    List<AuditLog> findByUserId(Long userId);

    List<AuditLog> findByAction(Action action);

    List<AuditLog> findByTimestampBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT a FROM AuditLog a WHERE a.entityType = :entityType AND a.entityId = :entityId ORDER BY a.timestamp DESC")
    List<AuditLog> findEntityHistory(@Param("entityType") String entityType, @Param("entityId") Long entityId);

    @Query("SELECT a FROM AuditLog a WHERE a.userId = :userId ORDER BY a.timestamp DESC")
    List<AuditLog> findUserActivity(@Param("userId") Long userId);
}
