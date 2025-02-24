package com.alwi.ecommerce.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class OrderRequest {
    @NotBlank(message = "User ID is required")
    private String userId;
    @NotBlank(message = "Status is required")
    @Size(max = 50, message = "Status must not exceed 50 characters")
    private String status;
    @NotBlank(message = "Product ID is required")
    private Long id;
}
