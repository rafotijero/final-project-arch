package com.ecommerce.auth.presentation.controller;

import com.ecommerce.auth.application.dto.LoginRequest;
import com.ecommerce.auth.application.dto.LoginResponse;
import com.ecommerce.auth.application.dto.MFASetupResponse;
import com.ecommerce.auth.application.dto.RegisterRequest;
import com.ecommerce.auth.application.dto.UserResponse;
import com.ecommerce.auth.application.service.AuthService;
import com.ecommerce.auth.domain.User;
import com.ecommerce.auth.domain.UserRepository;
import com.ecommerce.auth.infrastructure.security.JwtTokenProvider;
import com.ecommerce.auth.infrastructure.service.TOTPService;
import com.ecommerce.auth.application.exception.InvalidMfaCodeException;
import com.ecommerce.auth.application.exception.UserNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final TOTPService totpService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/mfa/setup")
    public ResponseEntity<MFASetupResponse> setupMfa(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(authService.setupMfa(userDetails.getUsername()));
    }

    @PostMapping("/mfa/enable")
    public ResponseEntity<String> enableMfa(@AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, String> body) {
        String code = body.get("code");
        authService.enableMfa(userDetails.getUsername(), code);
        return ResponseEntity.ok("MFA enabled successfully");
    }

    @PostMapping("/mfa/disable")
    public ResponseEntity<String> disableMfa(@AuthenticationPrincipal UserDetails userDetails) {
        authService.disableMfa(userDetails.getUsername());
        return ResponseEntity.ok("MFA disabled successfully");
    }

    @PostMapping("/verify-mfa")
    public ResponseEntity<LoginResponse> verifyMfa(@Valid @RequestBody LoginRequest request) {
        // Re-using login logic which handles MFA check
        return ResponseEntity.ok(authService.login(request));
    }

    /**
     * Verify MFA code for OAuth2 login
     * This endpoint validates the temporary session token and MFA code,
     * then returns a full JWT token for the user
     */
    @PostMapping("/oauth2/verify-mfa")
    public ResponseEntity<LoginResponse> verifyOAuth2MFA(@RequestBody Map<String, String> body) {
        String sessionToken = body.get("sessionToken");
        String mfaCode = body.get("mfaCode");

        // Validate and extract email from session token
        if (!jwtTokenProvider.validateToken(sessionToken)) {
            throw new RuntimeException("Invalid or expired session token");
        }

        String email = jwtTokenProvider.getUsername(sessionToken);

        // Fetch user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        // Validate MFA code
        try {
            int code = Integer.parseInt(mfaCode);
            if (!totpService.validateCode(user.getMfaSecret(), code)) {
                throw new InvalidMfaCodeException();
            }
        } catch (NumberFormatException e) {
            throw new InvalidMfaCodeException("Invalid MFA code format");
        }

        // Generate full token
        String token = jwtTokenProvider.generateToken(user.getEmail(), user.getRole().name(), user.getId().toString());

        // Build user response
        UserResponse userResponse = UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .provider(user.getProvider())
                .emailVerified(user.isEmailVerified())
                .mfaEnabled(user.isMfaEnabled())
                .role(user.getRole())
                .build();

        return ResponseEntity.ok(LoginResponse.builder()
                .token(token)
                .type("Bearer")
                .mfaRequired(false)
                .user(userResponse)
                .build());
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(authService.getCurrentUser(userDetails.getUsername()));
    }
}
