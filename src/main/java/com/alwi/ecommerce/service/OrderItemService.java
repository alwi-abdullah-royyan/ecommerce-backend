package com.alwi.ecommerce.service;

import com.alwi.ecommerce.dto.response.OrderItemResponse;
import com.alwi.ecommerce.exception.DataNotFoundException;
import com.alwi.ecommerce.model.Order;
import com.alwi.ecommerce.model.OrderItem;
import com.alwi.ecommerce.model.User;
import com.alwi.ecommerce.repository.OrderItemRepository;
import com.alwi.ecommerce.repository.OrderRepository;
import com.alwi.ecommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderItemService {
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrderRepository orderRepository;

    public List<OrderItemResponse> findByOrder(Long id, Authentication authentication) {
        UserDetails auth = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findUserByUsername(auth.getUsername())
                .orElseThrow(() -> new DataNotFoundException("Current user not found"));
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Order not found"));
        if (user.getRole().equals("ADMIN")) {
            return orderItemRepository.findByOrder(order).stream().
                    map(OrderItemService::convertToResponse).toList();
        } else if(user.getRole().equals("COSTUMER") && order.getUser().getId().equals(user.getId())) {
            return orderItemRepository.findByOrder(order).stream()
                    .map(OrderItemService::convertToResponse).toList();
        }
        throw new DataNotFoundException("Current user not found");
    }
    public static OrderItemResponse convertToResponse(OrderItem orderItem) {
        OrderItemResponse response = new OrderItemResponse();

        response.setId(orderItem.getId());
        response.setQty(orderItem.getQty());
        response.setSubtotal(orderItem.getSubtotal());

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
