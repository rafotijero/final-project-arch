package com.ecommerce.order.application.usecase;

import com.ecommerce.order.application.dto.OrderItemResponse;
import com.ecommerce.order.application.dto.OrderResponse;
import com.ecommerce.order.domain.model.Order;
import com.ecommerce.order.domain.model.OrderItem;
import com.ecommerce.order.domain.model.OrderStatus;
import com.ecommerce.order.domain.repository.OrderRepository;
import com.ecommerce.order.infrastructure.client.ProductServiceClient;
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
public class CancelOrderUseCase {
    private final OrderRepository orderRepository;
    private final ProductServiceClient productServiceClient;

    @Transactional
    public OrderResponse execute(UUID orderId, UUID userId) {
        Order order = orderRepository.findByIdWithItems(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));

        // Validar que el usuario sea el dueño de la orden
        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException("You can only cancel your own orders"); // Or specific UnauthorizedException
        }

        // Validar que la orden se pueda cancelar
        if (order.getStatus() == OrderStatus.DELIVERED ||
                order.getStatus() == OrderStatus.CANCELLED) {
            throw new InvalidOrderStatusException("Order cannot be cancelled");
        }

        // Devolver stock a los productos
        for (OrderItem item : order.getItems()) {
            productServiceClient.updateStock(
                    item.getProductId(),
                    item.getQuantity(),
                    true // es suma (devolver stock)
            );
        }

        order.setStatus(OrderStatus.CANCELLED);
        Order updated = orderRepository.save(order);

        log.info("Order {} cancelled successfully", orderId);

        return mapToResponse(updated);
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
