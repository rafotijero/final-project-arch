package com.ecommerce.order.domain.repository;

import com.ecommerce.order.domain.model.Order;
import com.ecommerce.order.domain.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findByUserId(UUID userId);

    List<Order> findByStatus(OrderStatus status);

    @Query("SELECT o FROM Order o WHERE o.userId = :userId AND o.status = :status")
    List<Order> findByUserIdAndStatus(UUID userId, OrderStatus status);

    @Query("SELECT o FROM Order o JOIN FETCH o.items WHERE o.id = :orderId")
    Optional<Order> findByIdWithItems(UUID orderId);
}
