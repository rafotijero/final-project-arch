package com.ecommerce.product.application.usecase;

import com.ecommerce.product.application.dto.CategoryRequest;
import com.ecommerce.product.application.dto.CategoryResponse;
import com.ecommerce.product.domain.model.Category;
import com.ecommerce.product.domain.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateCategoryUseCase {
    private final CategoryRepository categoryRepository;

    public CategoryResponse execute(CategoryRequest request) {
        if (categoryRepository.existsByName(request.getName())) {
            throw new RuntimeException("Category already exists");
        }

        Category category = Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();

        Category saved = categoryRepository.save(category);
        return mapToResponse(saved);
    }

    private CategoryResponse mapToResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .build();
    }
}
