package com.alwi.ecommerce.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CartRequest {

    @NotBlank
    private Long id;
    @NotBlank(message = "User ID is required")
    @Size(min = 36, max = 36, message = "User ID must be a valid UUID")
    private String userId;
    @NotBlank(message = "Username is required")
    @Size(max = 50, message = "Username must not exceed 50 characters")
    private String username;
    @NotNull(message = "Product ID is required")
    private Long productId;

    @NotNull(message = "Total is required")
    @Min(value = 1, message = "Total must be at least 1")
    private Long total;
}
