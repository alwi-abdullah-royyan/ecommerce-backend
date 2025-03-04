package com.alwi.ecommerce.controller;

import com.alwi.ecommerce.dto.request.OrderRequest;
import com.alwi.ecommerce.dto.response.ApiResponse;
import com.alwi.ecommerce.dto.response.ErrorResponse;
import com.alwi.ecommerce.dto.response.OrderResponse;
import com.alwi.ecommerce.dto.response.PaginatedResponse;
import com.alwi.ecommerce.exception.DataNotFoundException;
import com.alwi.ecommerce.exception.UnauthorizedException;
import com.alwi.ecommerce.exception.ValidationException;
import com.alwi.ecommerce.service.OrderService;
import com.alwi.ecommerce.util.OrderStatusUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @GetMapping("/all")
    private ResponseEntity<?> findAll(@RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "10") int size) {
        try {
            Page<OrderResponse> response = orderService.findAll(page, size);

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
    @GetMapping("/create")
    private ResponseEntity<?> create(Authentication authentication) {
        try{
            OrderResponse response = orderService.create(authentication);
            return ResponseEntity.ok(new ApiResponse<>(200, response));
        } catch(IllegalArgumentException e){
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.CONFLICT.value(),
                    "Status invalid",
                    e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
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
    @GetMapping("/filter")
    private ResponseEntity<?> findByFilter(@RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "10") int size,
                                           @RequestParam String status,
                                            Authentication authentication) {
       try{
           Page<OrderResponse> response = orderService.getOrderByStatus(page,size, status,authentication);
           return ResponseEntity.ok(new PaginatedResponse<>(200, response));
       } catch (DataNotFoundException e) {
           ErrorResponse errorResponse = new ErrorResponse(
                   HttpStatus.NOT_FOUND.value(),
                   "Data Not Found",
                   e.getMessage()
           );
           return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
       } catch(UnauthorizedException e){
           ErrorResponse errorResponse = new ErrorResponse(
                   HttpStatus.UNAUTHORIZED.value(),
                   "Unauthorized",
                   e.getMessage()
           );
           return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
       } catch (Exception e) {
           ErrorResponse errorResponse = new ErrorResponse(
                   HttpStatus.INTERNAL_SERVER_ERROR.value(),
                   "Internal Server Error",
                   "An unexpected error occurred."
           );
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
       }
   }
    @GetMapping("/user/filter")
    private ResponseEntity<?> findByUserFilter(@RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "10") int size,
                                               @RequestParam String status,
                                               Authentication authentication) {
       try{
           Page<OrderResponse> response = orderService.getOrderByUserAndStatus(page,size, status, authentication);
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
    @PutMapping("/update/{id}")
    private ResponseEntity<?> checkout(@PathVariable Long id, @RequestBody OrderRequest orderRequest) {
        try{
            OrderResponse response = orderService.updateStatus(id, orderRequest.getStatus());
            return ResponseEntity.ok(new ApiResponse<>(200, response));
        } catch (ValidationException e){
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.CONFLICT.value(),
                    "Status invalid",
                    e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
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
    @GetMapping("/user")
    private ResponseEntity<?> findByUser(@RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "10") int size,
                                         Authentication authentication) {
        try{
            Page<OrderResponse> response = orderService.getOrderByUser(page, size, authentication);
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
    @GetMapping("/detail/{id}")
    private ResponseEntity<?> findById(@PathVariable Long id,Authentication authentication) {
        try{
            OrderResponse response = orderService.findById(id, authentication);
            return ResponseEntity.ok(new ApiResponse<>(200, response));
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
    @GetMapping("/status_can_change_to/{id}")
    private ResponseEntity<?> statusCanChangeTo(@PathVariable Long id) {
        try{
            List<OrderStatusUtil.OrderStatus> response = orderService.getAllowedStatusesForOrder(id);
            return ResponseEntity.ok(new ApiResponse<>(200, response));
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

    @DeleteMapping("/delete/{id}")
    private ResponseEntity<?> delete(@PathVariable Long id) {
        try{
            orderService.delete(id);
            return ResponseEntity.ok(new ApiResponse<>(200, "Order deleted successfully."));
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
