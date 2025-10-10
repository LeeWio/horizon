package com.sunrizon.horizon.dto;

import jakarta.validation.constraints.Size;
import com.sunrizon.horizon.enums.ColumnStatus;
import lombok.Data;

@Data
public class UpdateColumnRequest {
    
    @Size(max = 100, message = "Column name must not exceed 100 characters")
    private String name;
    
    @Size(max = 150, message = "Column slug must not exceed 150 characters")
    private String slug;
    
    @Size(max = 500, message = "Column description must not exceed 500 characters")
    private String description;
    
    private String coverImage;
    
    private ColumnStatus status;
}