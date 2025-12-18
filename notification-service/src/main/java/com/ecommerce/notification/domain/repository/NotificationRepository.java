package com.ecommerce.notification.domain.repository;

import com.ecommerce.notification.domain.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByRecipient(String recipient);

    List<Notification> findByRelatedEntityTypeAndRelatedEntityId(String entityType, Long entityId);
}
