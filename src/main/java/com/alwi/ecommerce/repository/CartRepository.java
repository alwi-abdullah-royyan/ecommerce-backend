package com.alwi.ecommerce.repository;

import com.alwi.ecommerce.model.Cart;
import com.alwi.ecommerce.model.Product;
import com.alwi.ecommerce.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Page<Cart> findByUserId(UUID id, Pageable pageable);

    Optional<Cart> findByUserAndProduct(User user, Product product);
    List<Cart> findByCheckedAndUser(boolean checked, User user);

}
