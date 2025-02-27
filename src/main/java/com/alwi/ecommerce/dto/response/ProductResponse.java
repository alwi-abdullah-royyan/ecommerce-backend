package com.alwi.ecommerce.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
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