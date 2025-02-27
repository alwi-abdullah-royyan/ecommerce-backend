package com.alwi.ecommerce.repository;

import com.alwi.ecommerce.model.Order;
import com.alwi.ecommerce.model.OrderItem;
import com.alwi.ecommerce.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByOrder(Order order);
}
