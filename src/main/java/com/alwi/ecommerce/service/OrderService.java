package com.alwi.ecommerce.service;

import com.alwi.ecommerce.dto.response.OrderResponse;
import com.alwi.ecommerce.exception.DataNotFoundException;
import com.alwi.ecommerce.model.Order;
import com.alwi.ecommerce.model.Product;
import com.alwi.ecommerce.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;
    public Page<OrderResponse> findAll(int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Order> order = orderRepository.findAll(pageable);
            if (order.isEmpty()){
                throw new DataNotFoundException("Cart not found");
            }
            return order.map(OrderService::convertToResponse);
        } catch(DataNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error find all cart",e);
        }
    }
    public static OrderResponse convertToResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setUserId(order.getUser().getId());
        response.setUsername(order.getUser().getUsername());
        response.setStatus(order.getStatus());
        response.setCreatedAt(order.getCreatedAt());
        response.setUpdatedAt(order.getUpdatedAt());
        return response;
    }
}
