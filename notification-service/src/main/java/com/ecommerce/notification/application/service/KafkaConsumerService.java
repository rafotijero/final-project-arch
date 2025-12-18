package com.ecommerce.notification.application.service;

import com.ecommerce.notification.application.dto.OrderEventDTO;
import com.ecommerce.notification.application.dto.OrderItemDTO;
import com.ecommerce.notification.domain.model.AuditLog;
import com.ecommerce.notification.domain.model.Notification;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumerService {

    private final NotificationService notificationService;
    private final AuditService auditService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "order-events", groupId = "notification-service-group")
    public void consumeOrderEvent(String message, Acknowledgment acknowledgment) {
        try {
            log.info("Received order event: {}", message);

            OrderEventDTO orderEvent = objectMapper.readValue(message, OrderEventDTO.class);

            // Create audit log
            createOrderAuditLog(orderEvent);

            // Send notification email
            sendOrderNotification(orderEvent);

            // Acknowledge message
            acknowledgment.acknowledge();
            log.info("Order event processed successfully: {}", orderEvent.getOrderId());

        } catch (Exception e) {
            log.error("Error processing order event: {}", message, e);
            // Don't acknowledge - message will be reprocessed
        }
    }

    private void createOrderAuditLog(OrderEventDTO orderEvent) {
        AuditLog.Action action = switch (orderEvent.getEventType()) {
            case "ORDER_CREATED" -> AuditLog.Action.CREATE;
            case "ORDER_UPDATED" -> AuditLog.Action.UPDATE;
            case "ORDER_CANCELLED" -> AuditLog.Action.DELETE;
            default -> AuditLog.Action.OTHER;
        };

        String details = String.format(
                "Order %s - Status: %s - Total: $%.2f - Items: %d",
                orderEvent.getEventType(),
                orderEvent.getStatus(),
                orderEvent.getTotalAmount(),
                orderEvent.getItems() != null ? orderEvent.getItems().size() : 0);

        auditService.createAuditLog(
                "ORDER",
                orderEvent.getOrderId(),
                orderEvent.getUserId(),
                orderEvent.getUsername(),
                action,
                details,
                null,
                orderEvent.getStatus());
    }

    private void sendOrderNotification(OrderEventDTO orderEvent) {
        if (orderEvent.getUserEmail() == null || orderEvent.getUserEmail().isEmpty()) {
            log.warn("No email address for user: {}", orderEvent.getUserId());
            return;
        }

        Notification.NotificationType notificationType = switch (orderEvent.getEventType()) {
            case "ORDER_CREATED" -> Notification.NotificationType.ORDER_CREATED;
            case "ORDER_UPDATED" -> Notification.NotificationType.ORDER_UPDATED;
            case "ORDER_CANCELLED" -> Notification.NotificationType.ORDER_CANCELLED;
            default -> Notification.NotificationType.GENERAL;
        };

        String subject = generateEmailSubject(orderEvent);
        String body = generateEmailBody(orderEvent);

        notificationService.createAndSendNotification(
                orderEvent.getUserEmail(),
                subject,
                body,
                notificationType,
                orderEvent.getOrderId(),
                "ORDER",
                true // HTML email
        );
    }

    private String generateEmailSubject(OrderEventDTO orderEvent) {
        return switch (orderEvent.getEventType()) {
            case "ORDER_CREATED" -> "Order Confirmation - Order #" + orderEvent.getOrderId();
            case "ORDER_UPDATED" -> "Order Update - Order #" + orderEvent.getOrderId();
            case "ORDER_CANCELLED" -> "Order Cancelled - Order #" + orderEvent.getOrderId();
            default -> "Order Notification - Order #" + orderEvent.getOrderId();
        };
    }

    private String generateEmailBody(OrderEventDTO orderEvent) {
        StringBuilder html = new StringBuilder();

        html.append("<!DOCTYPE html>");
        html.append("<html><head><style>");
        html.append("body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }");
        html.append(".container { max-width: 600px; margin: 0 auto; padding: 20px; }");
        html.append(".header { background-color: #4CAF50; color: white; padding: 20px; text-align: center; }");
        html.append(".content { background-color: #f9f9f9; padding: 20px; }");
        html.append(".order-details { background-color: white; padding: 15px; margin: 15px 0; border-radius: 5px; }");
        html.append(".item { padding: 10px 0; border-bottom: 1px solid #eee; }");
        html.append(".total { font-size: 1.2em; font-weight: bold; color: #4CAF50; margin-top: 15px; }");
        html.append(".footer { text-align: center; padding: 20px; color: #777; font-size: 0.9em; }");
        html.append("</style></head><body>");

        html.append("<div class='container'>");
        html.append("<div class='header'><h1>").append(getEventTitle(orderEvent.getEventType())).append("</h1></div>");

        html.append("<div class='content'>");
        html.append("<p>Hello ").append(orderEvent.getUsername()).append(",</p>");
        html.append("<p>").append(getEventMessage(orderEvent.getEventType())).append("</p>");

        html.append("<div class='order-details'>");
        html.append("<h3>Order #").append(orderEvent.getOrderId()).append("</h3>");
        html.append("<p><strong>Status:</strong> ").append(orderEvent.getStatus()).append("</p>");
        html.append("<p><strong>Order Date:</strong> ").append(orderEvent.getCreatedAt()).append("</p>");

        if (orderEvent.getItems() != null && !orderEvent.getItems().isEmpty()) {
            html.append("<h4>Items:</h4>");
            for (OrderItemDTO item : orderEvent.getItems()) {
                html.append("<div class='item'>");
                html.append("<strong>").append(item.getProductName()).append("</strong><br>");
                html.append("Quantity: ").append(item.getQuantity());
                html.append(" × $").append(item.getPrice());
                html.append(" = $").append(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
                html.append("</div>");
            }
        }

        html.append("<div class='total'>Total: $").append(orderEvent.getTotalAmount()).append("</div>");
        html.append("</div>");

        html.append("<p>Thank you for your business!</p>");
        html.append("</div>");

        html.append("<div class='footer'>");
        html.append("<p>This is an automated message, please do not reply.</p>");
        html.append("<p>&copy; 2025 E-Commerce Platform. All rights reserved.</p>");
        html.append("</div>");

        html.append("</div></body></html>");

        return html.toString();
    }

    private String getEventTitle(String eventType) {
        return switch (eventType) {
            case "ORDER_CREATED" -> "Order Confirmed!";
            case "ORDER_UPDATED" -> "Order Updated";
            case "ORDER_CANCELLED" -> "Order Cancelled";
            default -> "Order Notification";
        };
    }

    private String getEventMessage(String eventType) {
        return switch (eventType) {
            case "ORDER_CREATED" -> "Your order has been successfully placed and is being processed.";
            case "ORDER_UPDATED" -> "Your order has been updated. Please review the details below.";
            case "ORDER_CANCELLED" ->
                "Your order has been cancelled. If you did not request this, please contact support.";
            default -> "This is a notification about your order.";
        };
    }
}
