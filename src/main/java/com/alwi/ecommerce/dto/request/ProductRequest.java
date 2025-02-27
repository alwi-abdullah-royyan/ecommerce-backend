package com.alwi.ecommerce.dto.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@Data
public class ProductRequest {
    private Long id;
    private String productName;
    private String description;
    private BigDecimal price;
    private Long categoryId;
    private MultipartFile image;
    private int qty;
}
