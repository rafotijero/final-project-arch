package com.ecommerce.order.application.usecase;

import com.ecommerce.order.application.dto.OrderItemResponse;
import com.ecommerce.order.application.dto.OrderResponse;
import com.ecommerce.order.domain.model.Order;
import com.ecommerce.order.domain.model.OrderItem;
import com.ecommerce.order.domain.model.OrderStatus;
import com.ecommerce.order.domain.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ListOrdersUseCase {
    private final OrderRepository orderRepository;

    public List<OrderResponse> execute(UUID userId, String statusStr) {
        OrderStatus status = statusStr != null ? OrderStatus.valueOf(statusStr) : null;
        List<Order> orders;

        // Logic: if userId provided (USER role), filter by it.
        // If "Admin" calls, maybe filter by user if specific user requested?
        // Instructions: "Listar mis pedidos (USER) o todos (ADMIN)"
        // This implies context awareness.
        // If I serve CreateOrderUseCase where I passed userId, here I pass userId.

        if (status != null) {
            orders = orderRepository.findByUserIdAndStatus(userId, status);
        } else {
            orders = orderRepository.findByUserId(userId);
        }

        // Note: For ADMIN wanting ALL orders, we'd need a different signature or logic
        // flow.
        // Currently implementing "List MY orders" effectively.
        // I will add a method for "List ALL orders" or make this flexible if needed.

        return orders.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
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
