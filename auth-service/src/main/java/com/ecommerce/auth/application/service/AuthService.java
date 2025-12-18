package com.ecommerce.auth.application.service;

import com.ecommerce.auth.application.dto.LoginRequest;
import com.ecommerce.auth.application.dto.LoginResponse;
import com.ecommerce.auth.application.dto.RegisterRequest;
import com.ecommerce.auth.application.dto.UserResponse;
import com.ecommerce.auth.application.exception.*;
import com.ecommerce.auth.domain.AuthProvider;
import com.ecommerce.auth.domain.Role;
import com.ecommerce.auth.domain.User;
import com.ecommerce.auth.domain.UserRepository;
import com.ecommerce.auth.infrastructure.security.JwtTokenProvider;
import com.ecommerce.auth.infrastructure.service.TOTPService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final TOTPService totpService;

    @Transactional
    public void register(RegisterRequest request) {
        log.info("Attempting to register user with email: {}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Registration failed: Email already exists - {}", request.getEmail());
            throw new EmailAlreadyExistsException(request.getEmail());
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .provider(AuthProvider.LOCAL)
                .emailVerified(false)
                .mfaEnabled(false)
                .build();

        User savedUser = userRepository.save(user);
        log.info("User registered successfully: {} with ID: {}", savedUser.getEmail(), savedUser.getId());
    }

    public LoginResponse login(LoginRequest request) {
        log.info("Login attempt for email: {}", request.getEmail());

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new UserNotFoundException(request.getEmail()));

            log.info("User authenticated successfully: {}, MFA enabled: {}", user.getEmail(), user.isMfaEnabled());

            if (user.isMfaEnabled()) {
                if (request.getMfaCode() == null || request.getMfaCode().isBlank()) {
                    log.info("MFA required for user: {}", user.getEmail());
                    return LoginResponse.builder()
                            .mfaRequired(true)
                            .build();
                }

                try {
                    int code = Integer.parseInt(request.getMfaCode());
                    if (!totpService.validateCode(user.getMfaSecret(), code)) {
                        log.warn("Invalid MFA code for user: {}", user.getEmail());
                        throw new InvalidMfaCodeException();
                    }
                    log.info("MFA code validated successfully for user: {}", user.getEmail());
                } catch (NumberFormatException e) {
                    log.warn("Invalid MFA code format for user: {}", user.getEmail());
                    throw new InvalidMfaCodeException("Invalid MFA code format");
                }
            }

            String token = jwtTokenProvider.generateToken(user.getEmail(), user.getRole().name(),
                    user.getId().toString());
            log.info("Login successful for user: {}", user.getEmail());

            UserResponse userResponse = UserResponse.builder()
                    .id(user.getId())
                    .email(user.getEmail())
                    .username(user.getUsername())
                    .provider(user.getProvider())
                    .emailVerified(user.isEmailVerified())
                    .mfaEnabled(user.isMfaEnabled())
                    .role(user.getRole())
                    .build();

            return LoginResponse.builder()
                    .token(token)
                    .type("Bearer")
                    .mfaRequired(false)
                    .user(userResponse)
                    .build();
        } catch (BadCredentialsException e) {
            log.warn("Invalid credentials for email: {}", request.getEmail());
            throw new InvalidCredentialsException();
        } catch (AuthenticationException e) {
            log.error("Authentication error for email: {}", request.getEmail(), e);
            throw e;
        }
    }

    @Transactional
    public com.ecommerce.auth.application.dto.MFASetupResponse setupMfa(String email) {
        log.info("Setting up MFA for user: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        String secret = totpService.generateSecret();
        String qrCodeUrl = totpService.generateQRCodeUrl(email, secret);
        user.setMfaSecret(secret);
        userRepository.save(user); // Temporary save of secret, but not enabled yet

        log.info("MFA secret generated for user: {}", email);

        return com.ecommerce.auth.application.dto.MFASetupResponse.builder()
                .secret(secret)
                .qrCodeUrl(qrCodeUrl)
                .build();
    }

    @Transactional
    public void enableMfa(String email, String code) {
        log.info("Attempting to enable MFA for user: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        if (user.getMfaSecret() == null) {
            log.warn("MFA enable failed: MFA not setup for user {}", email);
            throw new MfaNotSetupException();
        }

        try {
            if (!totpService.validateCode(user.getMfaSecret(), Integer.parseInt(code))) {
                log.warn("MFA enable failed: Invalid code for user {}", email);
                throw new InvalidMfaCodeException();
            }
        } catch (NumberFormatException e) {
            log.warn("MFA enable failed: Invalid code format for user {}", email);
            throw new InvalidMfaCodeException("Invalid MFA code format");
        }

        user.setMfaEnabled(true);
        userRepository.save(user);

        log.info("MFA enabled successfully for user: {}", email);
    }

    @Transactional
    public void disableMfa(String email) {
        log.info("Attempting to disable MFA for user: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        user.setMfaEnabled(false);
        user.setMfaSecret(null);
        userRepository.save(user);

        log.info("MFA disabled successfully for user: {}", email);
    }

    public UserResponse getCurrentUser(String email) {
        log.info("Fetching current user info for: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .provider(user.getProvider())
                .emailVerified(user.isEmailVerified())
                .mfaEnabled(user.isMfaEnabled())
                .role(user.getRole())
                .build();
    }
}
