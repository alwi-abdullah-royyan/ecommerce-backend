package com.alwi.ecommerce.service;

import com.alwi.ecommerce.dto.response.OrderItemResponse;
import com.alwi.ecommerce.exception.DataNotFoundException;
import com.alwi.ecommerce.model.OrderItem;
import com.alwi.ecommerce.repository.OrderItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class OrderItemService {
    @Autowired
    private OrderItemRepository orderItemRepository;
    public Page<OrderItemResponse> findAll(int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<OrderItem> orderItems = orderItemRepository.findAll(pageable);
            if (orderItems.isEmpty()){
                throw new DataNotFoundException("Cart not found");
            }
            return orderItems.map(OrderItemService::convertToResponse);
        } catch(DataNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error find all cart",e);
        }
    }
    public static OrderItemResponse convertToResponse(OrderItem orderItem) {
        OrderItemResponse response = new OrderItemResponse();

        response.setId(orderItem.getId());
        response.setTotal(orderItem.getTotal());

        // Set Order Data
        if (orderItem.getOrder() != null) {
            response.setOrderId(orderItem.getOrder().getId());
        }

        // Set Product Data
        if (orderItem.getProduct() != null) {
            response.setProductId(orderItem.getProduct().getId());
            response.setProductName(orderItem.getProduct().getName());
            response.setPrice(orderItem.getProduct().getPrice());
            response.setImage(orderItem.getProduct().getImage());
        }

        return response;
    }

}
