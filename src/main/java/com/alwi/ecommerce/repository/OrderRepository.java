package com.alwi.ecommerce.repository;

import com.alwi.ecommerce.model.Order;
import com.alwi.ecommerce.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);
    Optional<Order> findByUserAndId(User user, Long id);
}
