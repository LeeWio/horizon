package com.sunrizon.horizon.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import com.sunrizon.horizon.enums.SeriesStatus;
import lombok.Data;

@Data
public class CreateSeriesRequest {
    
    @NotBlank(message = "Series name is required")
    @Size(max = 100, message = "Series name must not exceed 100 characters")
    private String name;
    
    @Size(max = 150, message = "Series slug must not exceed 150 characters")
    private String slug;
    
    @Size(max = 500, message = "Series description must not exceed 500 characters")
    private String description;
    
    private String coverImage;
    
    private SeriesStatus status;
    
    @NotBlank(message = "Author ID is required")
    private String authorId;
}