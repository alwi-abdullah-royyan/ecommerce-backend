package com.alwi.ecommerce.controller;

import com.alwi.ecommerce.dto.request.CategoryRequest;
import com.alwi.ecommerce.dto.response.ApiResponse;
import com.alwi.ecommerce.dto.response.CategoryResponse;
import com.alwi.ecommerce.dto.response.ErrorResponse;
import com.alwi.ecommerce.dto.response.PaginatedResponse;
import com.alwi.ecommerce.exception.DataNotFoundException;
import com.alwi.ecommerce.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/all")
    public ResponseEntity<?> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<CategoryResponse> response = categoryService.findAll(page, size);
            return ResponseEntity.ok(new PaginatedResponse<>(200, response));
        } catch (DataNotFoundException e) {
            return buildErrorResponse(HttpStatus.NOT_FOUND, "Data Not Found", e.getMessage());
        } catch (Exception e) {
            return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", "An unexpected error occurred.");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        try {
            CategoryResponse category = categoryService.findById(id);
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), category));
        } catch (DataNotFoundException e) {
            return buildErrorResponse(HttpStatus.NOT_FOUND, "Data Not Found", e.getMessage());
        } catch (Exception e) {
            return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", "An unexpected error occurred.");
        }
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CategoryRequest categoryRequest) {
        try {
            CategoryResponse category = categoryService.create(categoryRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(HttpStatus.CREATED.value(), category));
        } catch (Exception e) {
            return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", "An unexpected error occurred.");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody CategoryRequest categoryRequest) {
        try {
            CategoryResponse category = categoryService.update(id, categoryRequest);
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), category));
        } catch (DataNotFoundException e) {
            return buildErrorResponse(HttpStatus.NOT_FOUND, "Data Not Found", e.getMessage());
        } catch (Exception e) {
            return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", "An unexpected error occurred.");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            categoryService.delete(id);
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Category deleted successfully."));
        } catch (DataNotFoundException e) {
            return buildErrorResponse(HttpStatus.NOT_FOUND, "Data Not Found", e.getMessage());
        } catch (Exception e) {
            return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", "An unexpected error occurred.");
        }
    }

    // Utility method for consistent error response
    private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, String message, String details) {
        ErrorResponse errorResponse = new ErrorResponse(status.value(), message, details);
        return ResponseEntity.status(status).body(errorResponse);
    }
}
