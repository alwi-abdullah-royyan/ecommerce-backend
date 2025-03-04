package com.alwi.ecommerce.repository;

import com.alwi.ecommerce.model.Order;
import com.alwi.ecommerce.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findByUserAndStatus(User user, String status, Pageable pageable);
    Page<Order> findByStatus(String status, Pageable pageable);
    Page<Order> findByUser(User user, Pageable pageable);
    Optional<Order> findByUserAndId(User user, Long id);
}
