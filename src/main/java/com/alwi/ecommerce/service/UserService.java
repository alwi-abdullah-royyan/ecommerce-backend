package com.alwi.ecommerce.service;

import com.alwi.ecommerce.dto.request.UserRequest;
import com.alwi.ecommerce.dto.response.UserResponse;
import com.alwi.ecommerce.exception.DataNotFoundException;
import com.alwi.ecommerce.exception.UnauthorizedException;
import com.alwi.ecommerce.exception.ValidationException;
import com.alwi.ecommerce.security.CustomUserDetails;
import com.alwi.ecommerce.model.User;
import com.alwi.ecommerce.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService implements UserDetailsService{
    @Autowired
    private UserRepository userRepository;
    //get all user
    @Autowired
    @Lazy
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository){
        this.userRepository =userRepository;
        this.passwordEncoder=new BCryptPasswordEncoder();
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByUsername(username)
                .orElseThrow(()-> new UsernameNotFoundException("User not found with username " + username));
        return new CustomUserDetails(user);
    }
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
    public UserResponse update(UUID id, UserRequest userRequest, Authentication authentication) {
        try {
            UserDetails auth = (UserDetails) authentication.getPrincipal();
            User userToUpdate = userRepository.findById(id)
                    .orElseThrow(() -> new DataNotFoundException("User not found"));

            User currentUser = userRepository.findUserByUsername(auth.getUsername())
                    .orElseThrow(() -> new DataNotFoundException("Current user not found"));

            boolean isAdmin = "ADMIN".equals(currentUser.getRole());
            boolean isSelf = userToUpdate.getUsername().equals(auth.getUsername());

            if (!isAdmin && !isSelf) {
                throw new UnauthorizedException("You are not authorized to update this user");
            }

            if (userRequest.getUsername() != null && !userToUpdate.getUsername().equals(userRequest.getUsername())) {
                if (userRepository.findUserByUsername(userRequest.getUsername()).isPresent())
                    throw new IllegalArgumentException("Username already exists");
                userToUpdate.setUsername(userRequest.getUsername());
            }

            if (userRequest.getEmail() != null && !userToUpdate.getEmail().equals(userRequest.getEmail())) {
                if (userRepository.findUserByEmail(userRequest.getEmail()).isPresent())
                    throw new IllegalArgumentException("Email already exists");
                userToUpdate.setEmail(userRequest.getEmail());
            }

            if (userRequest.getPassword() != null && userRequest.getConfirmPassword() != null) {
                if (!userRequest.getPassword().equals(userRequest.getConfirmPassword()))
                    throw new ValidationException("Password and confirm password do not match");
                if (userRequest.getPassword().length() < 8)
                    throw new ValidationException("Password must be at least 8 characters");
                userToUpdate.setPassword(passwordEncoder.encode(userRequest.getPassword()));
            }

            if (isAdmin && userRequest.getRole() != null) {
                userToUpdate.setRole(userRequest.getRole());
            }

            User updatedUser = userRepository.save(userToUpdate);
            return convertToResponse(updatedUser);

        } catch (IllegalArgumentException | ValidationException | DataNotFoundException | UnauthorizedException e) {
            throw e;
        } catch (Exception e) {
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
            user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
            User savedUser = userRepository.save(user);
            return convertToResponse(savedUser);
        } catch(ValidationException | IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error registering user", e);
        }
    }
    //delete
    public Boolean delete(UUID id, Authentication authentication) {
        try {
            UserDetails auth = (UserDetails) authentication.getPrincipal();
            User userToDelete = userRepository.findById(id)
                    .orElseThrow(() -> new DataNotFoundException("User not found"));

            User currentUser = userRepository.findUserByUsername(auth.getUsername())
                    .orElseThrow(() -> new DataNotFoundException("Current user not found"));

            boolean isAdmin = "ADMIN".equals(currentUser.getRole());
            boolean isSelf = userToDelete.getUsername().equals(auth.getUsername());

            if (!isAdmin && !isSelf) {
                throw new UnauthorizedException("You are not authorized to delete this user");
            }
            if (userToDelete.getRole().equals("ADMIN")){
                throw new UnauthorizedException("You cannot delete admin");
            }

            userRepository.deleteById(id);
            return true;
        } catch ( DataNotFoundException | UnauthorizedException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error deleting cart",e);
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
