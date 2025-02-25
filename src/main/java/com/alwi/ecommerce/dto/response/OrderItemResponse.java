package com.alwi.ecommerce.dto.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemResponse {

    private Long id;
    private OrderData order;
    private ProductData product;
    private String productName;
    private Integer qty;
    private BigDecimal subtotal;

    public OrderItemResponse(){
        this.product = new ProductData();
        this.order = new OrderData();
    }
    public void setProductId(Long id) {this.product.setId(id);}

    public void setProductName(String name) {
        this.product.setName(name);
    }

    public void setPrice(BigDecimal price) {
        this.product.setPrice(price);
    }

    public void setImage(String image) {
        this.product.setImage(image);
    }
    public void setOrderId(Long id) {
        this.order.setId(id);
    }
}

@Data
class OrderData {
    private Long id;
}
