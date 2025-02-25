package com.alwi.ecommerce.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class OrderResponse {
    private Long id;
    private UserData user;
    private String status;
    private BigDecimal totalPrice;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public OrderResponse(){
        this.user = new UserData();
    }
    public void setUserId(UUID id) {
        this.user.setId(id);
    }
    public void setUsername(String username) {
        this.user.setUsername(username);
    }
}
