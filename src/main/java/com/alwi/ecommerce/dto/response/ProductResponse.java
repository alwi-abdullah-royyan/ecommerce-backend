package com.alwi.ecommerce.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ProductResponse {

    private Long id;
    private String productName;
    private String description;
    private BigDecimal price;
    private CategoryData category;
    private String image;
    private Integer qty;
    private Boolean disabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ProductResponse(){
        this.category=new CategoryData();
    }
    public void setCategoryId(Long id) {
        this.category.setId(id);
    }
    public void setCategoryName(String name) {
        this.category.setName(name);
    }
}

@Data
class CategoryData{
    private Long id;
    private String name;
}