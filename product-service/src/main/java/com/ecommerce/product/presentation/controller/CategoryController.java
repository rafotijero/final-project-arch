package com.ecommerce.product.presentation.controller;

import com.ecommerce.product.application.dto.CategoryRequest;
import com.ecommerce.product.application.dto.CategoryResponse;
import com.ecommerce.product.application.usecase.CreateCategoryUseCase;
import com.ecommerce.product.application.usecase.ListCategoriesUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CreateCategoryUseCase createCategoryUseCase;
    private final ListCategoriesUseCase listCategoriesUseCase;

    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CategoryRequest request) {
        CategoryResponse response = createCategoryUseCase.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> listCategories() {
        return ResponseEntity.ok(listCategoriesUseCase.execute());
    }
}
