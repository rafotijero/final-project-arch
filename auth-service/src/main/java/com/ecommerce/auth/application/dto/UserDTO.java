package com.ecommerce.auth.application.dto;

import com.ecommerce.auth.domain.AuthProvider;
import com.ecommerce.auth.domain.Role;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class UserDTO {
    private UUID id;
    private String email;
    private String username;
    private AuthProvider provider;
    private Role role;
    private boolean mfaEnabled;
}
