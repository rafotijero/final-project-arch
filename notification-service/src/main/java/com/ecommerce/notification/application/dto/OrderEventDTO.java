package com.ecommerce.notification.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderEventDTO {
    private Long orderId;
    private Long userId;
    private String userEmail;
    private String username;
    private String status;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<OrderItemDTO> items;
    private String eventType; // ORDER_CREATED, ORDER_UPDATED, ORDER_CANCELLED
}
