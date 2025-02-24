package com.alwi.ecommerce.controller;

import com.alwi.ecommerce.dto.request.CartRequest;
import com.alwi.ecommerce.dto.response.ApiResponse;
import com.alwi.ecommerce.dto.response.CartResponse;
import com.alwi.ecommerce.dto.response.ErrorResponse;
import com.alwi.ecommerce.dto.response.PaginatedResponse;
import com.alwi.ecommerce.exception.DataNotFoundException;
import com.alwi.ecommerce.service.CartService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/cart")
public class CartController {
    @Autowired
    private CartService cartService;

    @GetMapping("/all")
    private ResponseEntity<?> findAll(@RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "10") int size) {
        try {
            Page<CartResponse> response = cartService.findAll(page, size);

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
    @GetMapping("/id/{id}")
    private ResponseEntity<?> findByUserId(@RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "10") int size,
                                            @PathVariable UUID id) {
        try {
            Page<CartResponse> response = cartService.findByUserId(id, page, size);
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
    @PostMapping("/manage")
    public ResponseEntity<?> manage(@RequestBody CartRequest cartRequest){
        try {
            return ResponseEntity.status(HttpStatus.CREATED.value())
                    .body(new ApiResponse<>(HttpStatus.CREATED.value(), cartService.createOrUpdateCart(cartRequest)));
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
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            boolean deleted = cartService.delete(id);
            if (deleted) {
                return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Cart deleted successfully."));
            } else {
                ErrorResponse errorResponse = new ErrorResponse(
                        HttpStatus.NOT_FOUND.value(),
                        "Not Found",
                        "Cart not found: " + id
                );
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }
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
