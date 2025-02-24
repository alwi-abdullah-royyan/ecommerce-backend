package com.alwi.ecommerce.service;

import com.alwi.ecommerce.dto.response.CartResponse;
import com.alwi.ecommerce.dto.response.UserResponse;
import com.alwi.ecommerce.exception.DataNotFoundException;
import com.alwi.ecommerce.model.Cart;
import com.alwi.ecommerce.model.User;
import com.alwi.ecommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    public Page<UserResponse> findAll(int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<User> user = userRepository.findAll(pageable);
            if (user.isEmpty()){
                throw new DataNotFoundException("Cart not found");
            }
            return user.map(UserService::convertToResponse);
        } catch(DataNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error find all cart",e);
        }
    }
    public static UserResponse convertToResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        return response;
    }

}
