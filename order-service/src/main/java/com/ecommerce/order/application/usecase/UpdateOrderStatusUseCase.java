package com.ecommerce.order.application.usecase;

import com.ecommerce.order.application.dto.OrderItemResponse;
import com.ecommerce.order.application.dto.OrderResponse;
import com.ecommerce.order.domain.model.Order;
import com.ecommerce.order.domain.model.OrderItem;
import com.ecommerce.order.domain.model.OrderStatus;
import com.ecommerce.order.domain.repository.OrderRepository;
import com.ecommerce.order.infrastructure.exception.InvalidOrderStatusException;
import com.ecommerce.order.infrastructure.exception.OrderNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UpdateOrderStatusUseCase {
    private final OrderRepository orderRepository;

    @Transactional
    public OrderResponse execute(UUID orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));

        // Validar transición de estado
        validateStatusTransition(order.getStatus(), newStatus);

        order.setStatus(newStatus);
        Order updated = orderRepository.save(order);

        log.info("Order {} status updated to {}", orderId, newStatus);

        return mapToResponse(updated);
    }

    private void validateStatusTransition(OrderStatus current, OrderStatus next) {
        // Lógica de validación de transiciones válidas
        if (current == OrderStatus.CANCELLED || current == OrderStatus.DELIVERED) {
            throw new InvalidOrderStatusException(
                    "Cannot change status from " + current);
        }

        // Definir transiciones válidas según lógica de negocio
    }

    private OrderResponse mapToResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .items(order.getItems().stream()
                        .map(this::mapItemToResponse)
                        .collect(Collectors.toList()))
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .shippingAddress(order.getShippingAddress())
                .notes(order.getNotes())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }

    private OrderItemResponse mapItemToResponse(OrderItem item) {
        return OrderItemResponse.builder()
                .id(item.getId())
                .productId(item.getProductId())
                .productName(item.getProductName())
                .unitPrice(item.getUnitPrice())
                .quantity(item.getQuantity())
                .subtotal(item.getSubtotal())
                .build();
    }
}
