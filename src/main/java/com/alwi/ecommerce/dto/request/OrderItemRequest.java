package com.alwi.ecommerce.dto.request;

import jakarta.validation.constraints.NotNull;

public class OrderItemRequest {
    @NotNull
    private Long id;
    @NotNull
    private Long productId;
}
