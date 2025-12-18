package com.ecommerce.auth.infrastructure.security;

import com.ecommerce.auth.domain.AuthProvider;
import com.ecommerce.auth.domain.Role;
import com.ecommerce.auth.domain.User;
import com.ecommerce.auth.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String provider = userRequest.getClientRegistration().getRegistrationId();
        String email;
        String name;

        // Handle different OAuth2 providers with different attribute structures
        if ("github".equals(provider)) {
            // GitHub attributes: login, email (can be null), name
            email = oAuth2User.getAttribute("email");
            name = oAuth2User.getAttribute("name");
            String login = oAuth2User.getAttribute("login");

            // If email is null (user has private email), use login@github.local
            if (email == null || email.isBlank()) {
                email = login + "@github.local";
                log.warn("GitHub user has private email. Using generated email: {}", email);
            }

            // If name is null, use login as name
            if (name == null || name.isBlank()) {
                name = login;
            }
        } else {
            // Google and other providers: email, name
            email = oAuth2User.getAttribute("email");
            name = oAuth2User.getAttribute("name");
        }

        log.info("OAuth2 login attempt - Provider: {}, Email: {}, Name: {}", provider, email, name);

        // Map provider string to AuthProvider enum
        AuthProvider authProvider = switch (provider.toLowerCase()) {
            case "google" -> AuthProvider.GOOGLE;
            case "github" -> AuthProvider.GITHUB;
            default -> AuthProvider.LOCAL;
        };

        // Check if user exists, if not create
        Optional<User> userOptional = userRepository.findByEmail(email);
        User user;
        if (userOptional.isPresent()) {
            user = userOptional.get();
            log.info("Existing user found: {}", email);
            // Update info if needed
            user.setUsername(name);
            userRepository.save(user);
        } else {
            log.info("Creating new user from OAuth2: {}", email);
            user = User.builder()
                    .email(email)
                    .username(name)
                    .provider(authProvider)
                    .role(Role.USER)
                    .emailVerified(true) // OAuth2 emails are verified
                    .mfaEnabled(false)
                    .build();
            userRepository.save(user);
            log.info("New OAuth2 user created: {} with ID: {}", user.getEmail(), user.getId());
        }

        return oAuth2User;
    }
}
