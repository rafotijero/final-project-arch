package com.ecommerce.product.domain.repository;

import com.ecommerce.product.domain.model.Product;
import com.ecommerce.product.domain.model.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
    List<Product> findByStatus(ProductStatus status);

    List<Product> findByCategoryId(UUID categoryId);

    List<Product> findByNameContainingIgnoreCase(String name);

    @Query("SELECT p FROM Product p WHERE p.stock > 0 AND p.status = 'ACTIVE'")
    List<Product> findAvailableProducts();
}
