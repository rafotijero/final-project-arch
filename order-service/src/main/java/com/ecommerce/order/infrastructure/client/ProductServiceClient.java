package com.ecommerce.order.infrastructure.client;

import com.ecommerce.order.application.dto.ProductDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceClient {
    private final WebClient webClient;

    @Value("${services.product-service-url}")
    private String productServiceUrl;

    public Optional<ProductDTO> getProduct(UUID productId) {
        try {
            ProductDTO product = webClient.get()
                    .uri(productServiceUrl + "/api/products/{id}", productId)
                    .retrieve()
                    .bodyToMono(ProductDTO.class)
                    .block();

            return Optional.ofNullable(product);
        } catch (Exception e) {
            log.error("Error getting product {}: {}", productId, e.getMessage());
            return Optional.empty();
        }
    }

    public void updateStock(UUID productId, Integer quantity, boolean isAddition) {
        try {
            // Get JWT token from current request context
            String token = getTokenFromContext();

            String uri = productServiceUrl + "/api/products/" + productId + "/stock" +
                    "?quantity=" + quantity + "&isAddition=" + isAddition;

            webClient.patch()
                    .uri(uri)
                    .header("Authorization", "Bearer " + token)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();

            log.info("Stock updated for product {}", productId);
        } catch (Exception e) {
            log.error("Error updating stock for product {}: {}", productId, e.getMessage());
            throw new RuntimeException("Failed to update product stock", e);
        }
    }

    private String getTokenFromContext() {
        // Extract token from SecurityContextHolder
        org.springframework.security.core.Authentication authentication = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication();

        if (authentication != null && authentication.getCredentials() != null) {
            return authentication.getCredentials().toString();
        }

        // Fallback: try to get from request attributes
        org.springframework.web.context.request.RequestAttributes requestAttributes = org.springframework.web.context.request.RequestContextHolder
                .getRequestAttributes();

        if (requestAttributes != null) {
            jakarta.servlet.http.HttpServletRequest request = ((org.springframework.web.context.request.ServletRequestAttributes) requestAttributes)
                    .getRequest();
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                return authHeader.substring(7);
            }
        }

        throw new RuntimeException("No JWT token found in security context");
    }
}
