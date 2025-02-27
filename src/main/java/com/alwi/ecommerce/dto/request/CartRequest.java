package com.alwi.ecommerce.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CartRequest {

    private Long id;
    private String userId;
    private String username;
    private Long productId;
    private Integer qty;
    private Boolean checked;
}
