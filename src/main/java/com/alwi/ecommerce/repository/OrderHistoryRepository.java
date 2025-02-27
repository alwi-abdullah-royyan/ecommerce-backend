package com.alwi.ecommerce.repository;

import com.alwi.ecommerce.model.Order;
import com.alwi.ecommerce.model.OrderHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderHistoryRepository extends JpaRepository<OrderHistory, Long> {
    List<OrderHistory> findByOrderOrderByDateAsc(Order order);
}
