package com.alwi.ecommerce.service;

import com.alwi.ecommerce.dto.response.OrderHistoryResponse;
import com.alwi.ecommerce.exception.DataNotFoundException;
import com.alwi.ecommerce.model.OrderHistory;
import com.alwi.ecommerce.repository.OrderHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class OrderHistoryService {
    @Autowired
    private OrderHistoryRepository orderHistoryRepository;
    public Page<OrderHistoryResponse> findAll(int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<OrderHistory> orderHistories = orderHistoryRepository.findAll(pageable);
            if (orderHistories.isEmpty()){
                throw new DataNotFoundException("Cart not found");
            }
            return orderHistories.map(OrderHistoryService::convertToResponse);
        } catch(DataNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error find all cart",e);
        }
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
