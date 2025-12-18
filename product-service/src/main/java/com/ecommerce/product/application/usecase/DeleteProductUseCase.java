package com.ecommerce.product.application.usecase;

import com.ecommerce.product.domain.model.Product;
import com.ecommerce.product.domain.model.ProductStatus;
import com.ecommerce.product.domain.repository.ProductRepository;
import com.ecommerce.product.infrastructure.exception.ProductNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeleteProductUseCase {
    private final ProductRepository productRepository;

    public void execute(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        // Soft delete
        product.setStatus(ProductStatus.INACTIVE);
        productRepository.save(product);
    }
}
