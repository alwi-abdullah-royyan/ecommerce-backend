package com.alwi.ecommerce.service;

import com.alwi.ecommerce.dto.request.CategoryRequest;
import com.alwi.ecommerce.dto.response.CategoryResponse;
import com.alwi.ecommerce.exception.DataNotFoundException;
import com.alwi.ecommerce.exception.ValidationException;
import com.alwi.ecommerce.model.Category;
import com.alwi.ecommerce.repository.CategoryRepository;
import jakarta.transaction.Transactional;
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
        Pageable pageable = PageRequest.of(page, size);
        return categoryRepository.findAll(pageable)
                .map(CategoryService::convertToResponse);
    }

    public CategoryResponse findById(Long id) {
        Category category = categoryRepository.findById(id).orElse(null);
        if (category == null){
            throw new DataNotFoundException("Cart not found");
        }
        return convertToResponse(category);
    }
    public CategoryResponse create (CategoryRequest categoryRequest) {
        if(categoryRequest.getName() == null)
            throw new DataNotFoundException("Category name is required");
        if (categoryRepository.existsByName(categoryRequest.getName().toUpperCase()))
            throw new ValidationException("Category with the same name already exists.");

        Category category = new Category();
        category.setName(categoryRequest.getName().toUpperCase());
        category = categoryRepository.save(category);
        return convertToResponse(category);
    }
    @Transactional
    public CategoryResponse update(Long id, CategoryRequest categoryRequest) {
        if(categoryRequest.getName() == null)
            throw new DataNotFoundException("Category name is required");
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Category not found"));

        category.setName(categoryRequest.getName().toUpperCase());
        category = categoryRepository.save(category);
        return convertToResponse(category);
    }
    @Transactional
    public void delete(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Category not found"));
        categoryRepository.delete(category);
    }
    public static CategoryResponse convertToResponse(Category category) {
        CategoryResponse response = new CategoryResponse();
        response.setId(category.getId());
        response.setName(category.getName());
        return response;
    }

}
