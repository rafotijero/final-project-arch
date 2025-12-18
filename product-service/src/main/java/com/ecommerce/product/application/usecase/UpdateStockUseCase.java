package com.ecommerce.product.application.usecase;

import com.ecommerce.product.application.dto.CategoryResponse;
import com.ecommerce.product.application.dto.ProductResponse;
import com.ecommerce.product.domain.model.Product;
import com.ecommerce.product.domain.model.ProductStatus;
import com.ecommerce.product.domain.repository.ProductRepository;
import com.ecommerce.product.infrastructure.exception.InsufficientStockException;
import com.ecommerce.product.infrastructure.exception.ProductNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UpdateStockUseCase {
    private final ProductRepository productRepository;

    public ProductResponse execute(UUID productId, Integer quantity, boolean isAddition) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        int newStock = isAddition ? product.getStock() + quantity : product.getStock() - quantity;

        if (newStock < 0) {
            throw new InsufficientStockException("Insufficient stock");
        }

        product.setStock(newStock);

        // Actualizar estado según stock
        if (newStock == 0) {
            product.setStatus(ProductStatus.OUT_OF_STOCK);
        } else if (product.getStatus() == ProductStatus.OUT_OF_STOCK) {
            product.setStatus(ProductStatus.ACTIVE);
        }

        Product updated = productRepository.save(product);
        return mapToResponse(updated);
    }

    private ProductResponse mapToResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .category(CategoryResponse.builder()
                        .id(product.getCategory().getId())
                        .name(product.getCategory().getName())
                        .description(product.getCategory().getDescription())
                        .build())
                .status(product.getStatus())
                .imageUrl(product.getImageUrl())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}
