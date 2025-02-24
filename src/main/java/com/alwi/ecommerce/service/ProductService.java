package com.alwi.ecommerce.service;

import com.alwi.ecommerce.dto.response.ProductResponse;
import com.alwi.ecommerce.dto.response.UserResponse;
import com.alwi.ecommerce.exception.DataNotFoundException;
import com.alwi.ecommerce.model.Product;
import com.alwi.ecommerce.model.User;
import com.alwi.ecommerce.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;
    public Page<ProductResponse> findAll(int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Product> product = productRepository.findAll(pageable);
            if (product.isEmpty()){
                throw new DataNotFoundException("Cart not found");
            }
            return product.map(ProductService::convertToResponse);
        } catch(DataNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error find all cart",e);
        }
    }
    public static ProductResponse convertToResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setProductName(product.getName());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());

        response.setCategoryName(product.getCategory().getName());
        response.setCategoryId(product.getCategory().getId());
        response.setImage(product.getImage());
        response.setTotal(product.getTotal());
        response.setDisabled(product.getDisabled());
        response.setCreatedAt(product.getCreatedAt());
        response.setUpdatedAt(product.getUpdatedAt());

        return response;
    }

}
