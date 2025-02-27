package com.alwi.ecommerce.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
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
