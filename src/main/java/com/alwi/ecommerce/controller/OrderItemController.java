package com.alwi.ecommerce.controller;

import com.alwi.ecommerce.dto.response.*;
import com.alwi.ecommerce.exception.DataNotFoundException;
import com.alwi.ecommerce.service.OrderItemService;
import com.alwi.ecommerce.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order_item")
public class OrderItemController {
    @Autowired
    private OrderItemService orderItemService;

    @GetMapping("/{id}")
    private ResponseEntity<?> findByOrder(@PathVariable("id") Long id, Authentication authentication) {
        try {
            List<OrderItemResponse> response = orderItemService.findByOrder(id, authentication);
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), response));
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
