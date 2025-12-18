package com.ecommerce.product.infrastructure.config;

import com.ecommerce.product.infrastructure.security.JwtAuthenticationFilter;
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
                                                // Endpoints públicos (solo lectura)
                                                .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()
                                                .requestMatchers(HttpMethod.GET, "/api/categories/**").permitAll()

                                                // Endpoints protegidos (crear/editar/eliminar)
                                                .requestMatchers(HttpMethod.POST, "/api/products/**").authenticated()
                                                .requestMatchers(HttpMethod.PUT, "/api/products/**").authenticated()
                                                .requestMatchers(HttpMethod.PATCH, "/api/products/**").authenticated()
                                                .requestMatchers(HttpMethod.DELETE, "/api/products/**").authenticated()
                                                .requestMatchers(HttpMethod.POST, "/api/categories/**").authenticated()

                                                .anyRequest().authenticated())
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }
}
