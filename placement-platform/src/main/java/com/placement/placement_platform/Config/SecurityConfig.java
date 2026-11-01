package com.placement.placement_platform.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
                .cors(cors -> {})
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth

                        // Admin pages
                        .requestMatchers(
                                "/applications.html",
                                "/admin-register.html",
                                "/admin-jobs.html",
                                "/admin-jobs.js"
                        ).hasRole("ADMIN")

                        .requestMatchers(HttpMethod.POST,"/auth/admin/register").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST,"/auth/admin/logout").hasRole("ADMIN")

                        // Admin Job Management APIs
                        .requestMatchers(HttpMethod.POST, "/jobs")
                        .hasRole("ADMIN")

                        .requestMatchers(HttpMethod.PUT, "/jobs/**")
                        .hasRole("ADMIN")

                        .requestMatchers(HttpMethod.DELETE, "/jobs/**")
                        .hasRole("ADMIN")

                        // Public pages
                        .requestMatchers("/my-applications.html").permitAll()

                        .requestMatchers(HttpMethod.GET, "/applications/my").authenticated()

                        .requestMatchers(
                                "/",
                                "/index.html",
                                "/login.html",
                                "/register.html",
                                "/student-profile.html",
                                "/admin-login.html",
                                "/jobs.html",
                                "/style.css",
                                "/script.js",
                                "/auth/**",
                                "/jobs",
                                "/jobs/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()

                        // Student and application APIs
                        .requestMatchers(
                                "/students/**",
                                "/applications",
                                "/applications/**"
                        ).authenticated()

                        // Dashboard protection
                        .anyRequest().authenticated()
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(List.of(
                "http://localhost:8080",
                "http://127.0.0.1:8080"
        ));

        config.setAllowedMethods(List.of(
                "GET", "POST", "PUT", "DELETE", "OPTIONS"
        ));

        config.setAllowedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", config);

        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}