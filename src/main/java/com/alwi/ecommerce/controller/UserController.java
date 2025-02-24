package com.alwi.ecommerce.controller;

import com.alwi.ecommerce.dto.response.CartResponse;
import com.alwi.ecommerce.dto.response.ErrorResponse;
import com.alwi.ecommerce.dto.response.PaginatedResponse;
import com.alwi.ecommerce.dto.response.UserResponse;
import com.alwi.ecommerce.exception.DataNotFoundException;
import com.alwi.ecommerce.service.CartService;
import com.alwi.ecommerce.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/all")
    private ResponseEntity<?> findAll(@RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "10") int size) {
        try {
            Page<UserResponse> response = userService.findAll(page, size);

            return ResponseEntity.ok(new PaginatedResponse<>(200, response));

        } catch (DataNotFoundException e) {
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.NOT_FOUND.value(),
                    "Data Not Found",
                    e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);

        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Internal Server Error",
                    "An unexpected error occurred."
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
