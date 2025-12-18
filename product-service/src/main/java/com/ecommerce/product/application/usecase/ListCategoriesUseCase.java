package com.ecommerce.product.application.usecase;

import com.ecommerce.product.application.dto.CategoryResponse;
import com.ecommerce.product.domain.model.Category;
import com.ecommerce.product.domain.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ListCategoriesUseCase {
    private final CategoryRepository categoryRepository;

    public List<CategoryResponse> execute() {
        return categoryRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private CategoryResponse mapToResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .build();
    }
}
