package com.alwi.ecommerce.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderHistoryResponse {
    private Long id;
    private String status;
    private OrderData order;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime date;
    public OrderHistoryResponse(){this.order = new OrderData();}

    public void setOrderId(Long id) {this.order.setId(id);}
}
