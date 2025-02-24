package com.alwi.ecommerce.dto.request;

import jakarta.validation.constraints.NotNull;

public class OrderHistoryRequest {
    @NotNull
    private Long id;
}
