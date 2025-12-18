package com.ecommerce.order.application.service;

import com.ecommerce.order.domain.model.Order;
import com.ecommerce.order.domain.model.OrderItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String ORDER_EVENTS_TOPIC = "order-events";

    public void publishOrderCreatedEvent(Order order, String userEmail, String username) {
        try {
            Map<String, Object> event = buildOrderEvent(order, userEmail, username, "ORDER_CREATED");

            kafkaTemplate.send(ORDER_EVENTS_TOPIC, order.getId().toString(), event);
            log.info("Published ORDER_CREATED event for order: {}", order.getId());
        } catch (Exception e) {
            log.error("Failed to publish ORDER_CREATED event for order: {}", order.getId(), e);
        }
    }

    public void publishOrderUpdatedEvent(Order order, String userEmail, String username) {
        try {
            Map<String, Object> event = buildOrderEvent(order, userEmail, username, "ORDER_UPDATED");

            kafkaTemplate.send(ORDER_EVENTS_TOPIC, order.getId().toString(), event);
            log.info("Published ORDER_UPDATED event for order: {}", order.getId());
        } catch (Exception e) {
            log.error("Failed to publish ORDER_UPDATED event for order: {}", order.getId(), e);
        }
    }

    public void publishOrderCancelledEvent(Order order, String userEmail, String username) {
        try {
            Map<String, Object> event = buildOrderEvent(order, userEmail, username, "ORDER_CANCELLED");

            kafkaTemplate.send(ORDER_EVENTS_TOPIC, order.getId().toString(), event);
            log.info("Published ORDER_CANCELLED event for order: {}", order.getId());
        } catch (Exception e) {
            log.error("Failed to publish ORDER_CANCELLED event for order: {}", order.getId(), e);
        }
    }

    private Map<String, Object> buildOrderEvent(Order order, String userEmail, String username, String eventType) {
        Map<String, Object> event = new HashMap<>();
        event.put("orderId", order.getId());
        event.put("userId", order.getUserId());
        event.put("userEmail", userEmail);
        event.put("username", username);
        event.put("status", order.getStatus().name());
        event.put("totalAmount", order.getTotalAmount());
        event.put("createdAt", order.getCreatedAt());
        event.put("updatedAt", order.getUpdatedAt());
        event.put("eventType", eventType);

        List<Map<String, Object>> items = order.getItems().stream()
                .map(this::buildOrderItemMap)
                .collect(Collectors.toList());
        event.put("items", items);

        return event;
    }

    private Map<String, Object> buildOrderItemMap(OrderItem item) {
        Map<String, Object> itemMap = new HashMap<>();
        itemMap.put("productId", item.getProductId());
        itemMap.put("productName", item.getProductName());
        itemMap.put("quantity", item.getQuantity());
        itemMap.put("price", item.getUnitPrice());
        return itemMap;
    }
}
