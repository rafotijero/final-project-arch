package com.ecommerce.auth.application.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {
    private String token;
    private String type;
    private boolean mfaRequired;
    private UserResponse user;
}
