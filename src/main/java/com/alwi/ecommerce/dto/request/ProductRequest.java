package com.alwi.ecommerce.dto.request;

import jakarta.validation.constraints.NotNull;

public class ProductRequest {
    @NotNull
    private Long id;

    private Boolean disabled;
}
