package com.ecommerce.order.application.usecase;

import com.ecommerce.order.application.dto.*;
import com.ecommerce.order.domain.model.Order;
import com.ecommerce.order.domain.model.OrderItem;
import com.ecommerce.order.domain.model.OrderStatus;
import com.ecommerce.order.domain.repository.OrderRepository;
import com.ecommerce.order.infrastructure.client.ProductServiceClient;
import com.ecommerce.order.infrastructure.exception.ProductNotAvailableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateOrderUseCase {
        private final OrderRepository orderRepository;
        private final ProductServiceClient productServiceClient;
        private final com.ecommerce.order.application.service.OrderEventPublisher orderEventPublisher;

        @Transactional
        public OrderResponse execute(CreateOrderRequest request, UUID userId) {
                // 1. Validar productos y obtener información
                List<OrderItem> orderItems = new ArrayList<>();

                for (OrderItemRequest itemRequest : request.getItems()) {
                        ProductDTO product = productServiceClient.getProduct(itemRequest.getProductId())
                                        .orElseThrow(() -> new ProductNotAvailableException(
                                                        "Product not found: " + itemRequest.getProductId()));

                        log.info("Retrieved product: id={}, name={}, price={}, stock={}",
                                        product.getId(), product.getName(), product.getPrice(), product.getStock());

                        // 2. Validar stock disponible
                        if (product.getStock() < itemRequest.getQuantity()) {
                                throw new ProductNotAvailableException(
                                                "Insufficient stock for product: " + product.getName());
                        }

                        // 3. Crear item de pedido
                        OrderItem item = OrderItem.builder()
                                        .productId(product.getId())
                                        .productName(product.getName())
                                        .unitPrice(product.getPrice())
                                        .quantity(itemRequest.getQuantity())
                                        .build();

                        log.info("Created OrderItem: productId={}, unitPrice={}, quantity={}",
                                        item.getProductId(), item.getUnitPrice(), item.getQuantity());

                        orderItems.add(item);
                }

                // 4. Crear orden
                Order order = Order.builder()
                                .userId(userId)
                                .shippingAddress(request.getShippingAddress())
                                .notes(request.getNotes())
                                .status(OrderStatus.PENDING)
                                .build();

                // 5. Agregar items
                orderItems.forEach(order::addItem);
                order.calculateTotal();

                // 6. Guardar orden
                Order savedOrder = orderRepository.save(order);

                // 7. Actualizar stock de productos
                for (OrderItemRequest itemRequest : request.getItems()) {
                        productServiceClient.updateStock(
                                        itemRequest.getProductId(),
                                        itemRequest.getQuantity(),
                                        false // es resta
                        );
                }

                log.info("Order created successfully: {}", savedOrder.getId());

                // 8. Publish order created event to Kafka
                // TODO: Get user email from auth service or pass it from controller
                String userEmail = "user@example.com"; // Placeholder
                String username = "User"; // Placeholder
                orderEventPublisher.publishOrderCreatedEvent(savedOrder, userEmail, username);

                return mapToResponse(savedOrder);
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
