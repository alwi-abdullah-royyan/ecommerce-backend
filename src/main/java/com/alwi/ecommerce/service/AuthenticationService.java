package com.alwi.ecommerce.service;

import com.alwi.ecommerce.dto.request.AuthRequest;
import com.alwi.ecommerce.dto.response.AuthResponse;
import com.alwi.ecommerce.dto.response.UserResponse;
import com.alwi.ecommerce.exception.DataNotFoundException;
import com.alwi.ecommerce.model.User;
import com.alwi.ecommerce.repository.UserRepository;
import com.alwi.ecommerce.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    public AuthResponse login(AuthRequest authRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequest.getUsername(),
                            authRequest.getPassword()
                    )
            );
            if (authentication.isAuthenticated()) {
                User user = userRepository.findUserByUsername(authRequest.getUsername())
                        .orElseThrow(() -> new DataNotFoundException("User not found"));

                String token = jwtUtil.generateToken(user);
                return new AuthResponse(token);
            } else {
                throw new RuntimeException("Invalid authentication");
            }

        } catch(DataNotFoundException e){
            throw e;
        }catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error logging in", e);
        }
    }
    public UserResponse getCurrentUser(Authentication authentication) {
        try{
            String username = authentication.getName();

            User user = userRepository.findUserByUsername(username)
                    .orElseThrow(() -> new DataNotFoundException("User not found"));
            return convertToResponse(user);
        } catch(DataNotFoundException e) {
            throw e;
        }catch (Exception e) {
            throw new RuntimeException("Error getting current user", e);
        }
    }

    //refresh token
    public AuthResponse refreshToken(Authentication authentication) {
        UserDetails auth = (UserDetails) authentication.getPrincipal();
        User currentUser = userRepository.findUserByUsername(auth.getUsername())
                .orElseThrow(() -> new DataNotFoundException("Current user not found"));
        String token = jwtUtil.generateToken(currentUser);
        return new AuthResponse(token);
    }

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
