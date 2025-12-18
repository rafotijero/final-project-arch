package com.ecommerce.order.application.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class CategoryDTO {
    private UUID id;
    private String name;
    private String description;
}
