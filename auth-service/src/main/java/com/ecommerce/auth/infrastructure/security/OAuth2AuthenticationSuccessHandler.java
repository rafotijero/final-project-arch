package com.ecommerce.auth.infrastructure.security;

import com.ecommerce.auth.domain.User;
import com.ecommerce.auth.domain.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

        private final JwtTokenProvider jwtTokenProvider;
        private final UserRepository userRepository;

        @Value("${frontend.url:http://localhost:3000}")
        private String frontendUrl;

        @Override
        public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                        Authentication authentication) throws IOException, ServletException {

                // Get OAuth2User from authentication principal
                OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

                // Extract email from OAuth2User attributes
                String email = oAuth2User.getAttribute("email");

                log.info("OAuth2 authentication successful for user: {}", email);

                // Fetch user from database to get username and role
                User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new RuntimeException(
                                                "User not found after OAuth2 authentication: " + email));

                String targetUrl;

                // Check if user has MFA enabled
                if (user.isMfaEnabled()) {
                        log.info("User {} has MFA enabled, redirecting to MFA verification", email);

                        // Generate temporary token for MFA verification (5 minutes)
                        String sessionToken = jwtTokenProvider.generateTemporaryToken(user.getEmail());

                        // Redirect to MFA verification page
                        targetUrl = UriComponentsBuilder.fromUriString(frontendUrl + "/oauth2/mfa-verify")
                                        .queryParam("sessionToken", sessionToken)
                                        .queryParam("email", URLEncoder.encode(user.getEmail(), StandardCharsets.UTF_8))
                                        .build().toUriString();
                } else {
                        log.info("User {} does not have MFA, proceeding with normal OAuth flow", email);

                        // Generate token with email (not Google ID) so that subsequent API calls work
                        // correctly
                        String token = jwtTokenProvider.generateToken(user.getEmail(), user.getRole().name(),
                                        user.getId().toString());
                        log.debug("Generated JWT token for OAuth2 user: {}", email);

                        // Include username and email as fallback data in case /api/auth/me fails on
                        // frontend
                        targetUrl = UriComponentsBuilder.fromUriString(frontendUrl + "/oauth2/callback")
                                        .queryParam("token", token)
                                        .queryParam("username",
                                                        URLEncoder.encode(user.getUsername(), StandardCharsets.UTF_8))
                                        .queryParam("email", URLEncoder.encode(user.getEmail(), StandardCharsets.UTF_8))
                                        .build().toUriString();
                }

                log.info("Redirecting OAuth2 user {} to: {}", email,
                                targetUrl.substring(0, Math.min(targetUrl.length(), 100)));
                getRedirectStrategy().sendRedirect(request, response, targetUrl);
        }
}
