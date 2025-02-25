package com.alwi.ecommerce.service;

import com.alwi.ecommerce.dto.request.UserRequest;
import com.alwi.ecommerce.dto.response.CartResponse;
import com.alwi.ecommerce.dto.response.UserResponse;
import com.alwi.ecommerce.exception.DataNotFoundException;
import com.alwi.ecommerce.exception.ValidationException;
import com.alwi.ecommerce.model.Cart;
import com.alwi.ecommerce.model.User;
import com.alwi.ecommerce.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    //get all user
    public Page<UserResponse> findAll(int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<User> user = userRepository.findAll(pageable);
            if (user.isEmpty()){
                throw new DataNotFoundException("Cart not found");
            }
            return user.map(this::convertToResponse);
        } catch(DataNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error find all cart",e);
        }
    }
    //get user by id
    public UserResponse findById(UUID id) {
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new DataNotFoundException("User not found"));
            return convertToResponse(user);
        } catch (DataNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error find user by id", e);
        }
    }

    //update user
    @Transactional
    public UserResponse update(UUID id, UserRequest userRequest) {
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new DataNotFoundException("User not found"));
            if(userRequest.getUsername() != null) {
                if(userRepository.findUserByUsername(userRequest.getUsername()).isPresent())
                    throw new IllegalArgumentException("Username already exists");
                user.setUsername(userRequest.getUsername());
            }
            if(userRequest.getEmail() != null){
                if(userRepository.findUserByEmail(userRequest.getEmail()).isPresent())
                    throw new IllegalArgumentException("Email already exists");
                user.setEmail(userRequest.getEmail());
            }
            if(userRequest.getPassword() != null && userRequest.getConfirmPassword() != null){
                if(!userRequest.getPassword().equals(userRequest.getConfirmPassword()))
                    throw new ValidationException("Password and confirm password do not match");
                if(userRequest.getPassword().length() < 8)
                    throw new ValidationException("Password must be at least 8 characters");
                //TODO : add password encoder
                user.setPassword(userRequest.getPassword());
            }
            if(userRequest.getRole() != null && !user.getRole().equals("ADMIN")
                    && userRequest.getRole().equals("ADMIN")){
                user.setRole(userRequest.getRole());
            }
            User updatedUser = userRepository.save(user);
            return convertToResponse(updatedUser);
        } catch(IllegalArgumentException | ValidationException | DataNotFoundException e){
            throw e;
        }catch (Exception e) {
            throw new RuntimeException("Error updating user", e);
        }
    }

    //register user
    @Transactional
    public UserResponse register(UserRequest userRequest) {
        try {
            if(userRepository.findUserByUsername(userRequest.getUsername()).isPresent())
                throw new IllegalArgumentException("Username already exists");
            if(userRepository.findUserByEmail(userRequest.getEmail()).isPresent())
                throw new IllegalArgumentException("Email already exists");
            if(!userRequest.getPassword().equals(userRequest.getConfirmPassword()))
                throw new ValidationException("Password and confirm password do not match");
            if(userRequest.getPassword().length() < 8)
                throw new ValidationException("Password must be at least 8 characters");
            User user = new User();
            user.setUsername(userRequest.getUsername());
            user.setEmail(userRequest.getEmail());
            //TODO : add password encoder
            user.setPassword(userRequest.getPassword());
            User savedUser = userRepository.save(user);
            return convertToResponse(savedUser);
        } catch(ValidationException | IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error registering user", e);
        }
    }

    //convert to response
    public UserResponse convertToResponse(User user) {
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
