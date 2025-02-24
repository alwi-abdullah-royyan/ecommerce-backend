package com.alwi.ecommerce.service;

import com.alwi.ecommerce.dto.response.CategoryResponse;
import com.alwi.ecommerce.exception.DataNotFoundException;
import com.alwi.ecommerce.model.Category;
import com.alwi.ecommerce.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;
    public Page<CategoryResponse> findAll(int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Category> categories = categoryRepository.findAll(pageable);
            if (categories.isEmpty()){
                throw new DataNotFoundException("Cart not found");
            }
            return categories.map(CategoryService::convertToResponse);
        } catch(DataNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error find all cart",e);
        }
    }
    public static CategoryResponse convertToResponse(Category category) {
        CategoryResponse response = new CategoryResponse();
        response.setId(category.getId());
        response.setName(category.getName());
        return response;
    }

}
