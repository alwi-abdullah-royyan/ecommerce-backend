package com.alwi.ecommerce.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class CartResponse {

    private Long id;
    private UserData user;
    private ProductData product;
    private BigDecimal price;
    private Integer qty;
    private Boolean checked;
    public CartResponse(){
        this.user = new UserData();
        this.product=new ProductData();
    }
    public void setProductId(Long id) {this.product.setId(id);}

    public void setProductName(String name) {
        this.product.setName(name);
    }

    public void setProductPrice(BigDecimal price) {
        this.product.setPrice(price);
    }

    public void setProductImage(String image) {
        this.product.setImage(image);
    }
    public void setUserId(UUID id) {
        this.user.setId(id);
    }
    public void setUserName(String username) {
        this.user.setUsername(username);
    }

}

@Data
class ProductData {
    private Long id;
    private String name;
    private BigDecimal price;
    private String image;
}

@Data
class UserData{
    private UUID id;
    private String username;
}