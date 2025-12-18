package com.ecommerce.product.application.usecase;

import com.ecommerce.product.application.dto.CategoryResponse;
import com.ecommerce.product.application.dto.ProductRequest;
import com.ecommerce.product.application.dto.ProductResponse;
import com.ecommerce.product.domain.model.Category;
import com.ecommerce.product.domain.model.Product;
import com.ecommerce.product.domain.repository.CategoryRepository;
import com.ecommerce.product.domain.repository.ProductRepository;
import com.ecommerce.product.infrastructure.exception.CategoryNotFoundException;
import com.ecommerce.product.infrastructure.exception.ProductNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UpdateProductUseCase {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductResponse execute(UUID id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new CategoryNotFoundException("Category not found"));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setCategory(category);
        product.setImageUrl(request.getImageUrl());

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
