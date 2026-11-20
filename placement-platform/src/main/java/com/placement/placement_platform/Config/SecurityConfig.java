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
                                "/admin-jobs.js",
                                "/admin-students.html",
                                "/admin-coding-questions.html",
                                        "/admin-coding-questions.js"

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
                                "/coding-questions.html",
                                "/coding-questions.css",
                                "/coding-questions.js",
                                "/login.html",
                                "/register.html",
                                "/student-profile.html",
                                "/change-password.html",
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
                        // Admin Student Management APIs
                        .requestMatchers("/admin/students/**")
                        .hasRole("ADMIN")
                        // Student and application APIs
                        .requestMatchers(
                                "/students/**",
                                "/applications",
                                "/applications/**"
                        ).authenticated()
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/coding/questions/*/test-cases"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/coding/questions/*/test-cases"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/coding/questions/*/test-cases/*"
                        ).hasRole("ADMIN")
                        // Coding Questions APIs
                        .requestMatchers(HttpMethod.GET, "/api/coding/questions/**").permitAll()
                        // Student Solved Questions API
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/coding/solved"
                        ).authenticated()
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/coding/questions"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/coding/questions/**"
                        ).hasRole("ADMIN")
                        // Code Execution API
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/coding/execute"
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
                "http://localhost:63342",
                "http://127.0.0.1:63342"
        ));
        config.setAllowCredentials(true);
        config.setAllowedHeaders(List.of("*"));

        config.setAllowedMethods(List.of(
                "GET", "POST", "PUT", "DELETE", "OPTIONS"
        ));


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