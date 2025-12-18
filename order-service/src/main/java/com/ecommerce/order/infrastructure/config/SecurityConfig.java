package com.ecommerce.order.infrastructure.config;

import com.ecommerce.order.infrastructure.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
        private final JwtAuthenticationFilter jwtAuthFilter;
        private final org.springframework.web.cors.CorsConfigurationSource corsConfigurationSource;

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                                .csrf(AbstractHttpConfigurer::disable)
                                .authorizeHttpRequests(auth -> auth
                                                // Todos los endpoints de orders requieren autenticación
                                                .requestMatchers("/api/orders/**").authenticated()

                                                // Admin puede ver todas las órdenes y cambiar estados
                                                // Assuming "ADMIN" equates to "ROLE_ADMIN".
                                                // Note: JwtTokenProvider adds "ROLE_" prefix.
                                                // So .hasRole("ADMIN") works because it checks for authority
                                                // "ROLE_ADMIN".
                                                .requestMatchers(HttpMethod.GET, "/api/orders").hasRole("ADMIN")
                                                .requestMatchers(HttpMethod.PATCH, "/api/orders/*/status")
                                                .hasRole("ADMIN")

                                                .anyRequest().authenticated())
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }
}
