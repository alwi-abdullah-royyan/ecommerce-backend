package com.alwi.ecommerce.dto.request;

import lombok.Data;

@Data
public class ProductFilterRequest {
    private String category;
    private Double minPrice;
    private Double maxPrice;
    private String name;
}
