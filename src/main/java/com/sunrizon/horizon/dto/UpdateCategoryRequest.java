package com.sunrizon.horizon.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateCategoryRequest {
    
    @Size(max = 50, message = "Category name must not exceed 50 characters")
    private String name;
    
    @Size(max = 100, message = "Category slug must not exceed 100 characters")
    private String slug;
    
    @Size(max = 200, message = "Category description must not exceed 200 characters")
    private String description;
    
    private String parentId; // ID of parent category for hierarchical structure
}