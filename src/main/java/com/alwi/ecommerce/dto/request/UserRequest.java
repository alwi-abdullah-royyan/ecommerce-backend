package com.alwi.ecommerce.dto.request;

import lombok.Data;

import java.util.UUID;

@Data
public class UserRequest {
    private String username;
    private UUID id;
    private String email;
    private String password;
    private String confirmPassword;
    private String role;
}
