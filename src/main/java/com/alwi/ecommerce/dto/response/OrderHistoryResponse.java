package com.alwi.ecommerce.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderHistoryResponse {
    private Long id;
    private String status;
    private OrderData order;
    private LocalDateTime date;
    public OrderHistoryResponse(){this.order = new OrderData();}

    public void setOrderId(Long id) {this.order.setId(id);}
}
