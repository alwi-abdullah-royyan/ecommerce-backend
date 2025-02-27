package com.alwi.ecommerce.service;

import com.alwi.ecommerce.dto.response.OrderHistoryResponse;
import com.alwi.ecommerce.dto.response.OrderItemResponse;
import com.alwi.ecommerce.exception.DataNotFoundException;
import com.alwi.ecommerce.model.Order;
import com.alwi.ecommerce.model.OrderHistory;
import com.alwi.ecommerce.model.User;
import com.alwi.ecommerce.repository.OrderHistoryRepository;
import com.alwi.ecommerce.repository.OrderRepository;
import com.alwi.ecommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderHistoryService {
    @Autowired
    private OrderHistoryRepository orderHistoryRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrderRepository orderRepository;


    public List<OrderHistoryResponse> findByOrder(Long id, Authentication authentication) {
        UserDetails auth = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findUserByUsername(auth.getUsername())
                .orElseThrow(() -> new DataNotFoundException("Current user not found"));
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Order not found"));
        if (user.getRole().equals("ADMIN")) {
            return orderHistoryRepository.findByOrderOrderByDateAsc(order).stream()
                    .map(OrderHistoryService::convertToResponse).toList();
        } else if(user.getRole().equals("COSTUMER") && order.getUser().getId().equals(user.getId())){
            return orderHistoryRepository.findByOrderOrderByDateAsc(order).stream()
                    .map(OrderHistoryService::convertToResponse).toList();
        }
        throw new DataNotFoundException("Unable to get order history");
    }

public void create(Order order){
    OrderHistory orderHistory = new OrderHistory();
    orderHistory.setStatus(order.getStatus());
    orderHistory.setOrder(order);
    orderHistoryRepository.save(orderHistory);
}
    public static OrderHistoryResponse convertToResponse(OrderHistory orderHistory) {
        OrderHistoryResponse response = new OrderHistoryResponse();

        response.setId(orderHistory.getId());
        response.setStatus(orderHistory.getStatus());
        response.setDate(orderHistory.getDate());

        if (orderHistory.getOrder() != null) {
            response.setOrderId(orderHistory.getOrder().getId());
        }

        return response;
    }

}
