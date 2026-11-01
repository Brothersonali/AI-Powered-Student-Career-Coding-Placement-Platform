package com.placement.placement_platform.controller;

import com.placement.placement_platform.entity.Admin;
import com.placement.placement_platform.repository.AdminRepository;
import com.placement.placement_platform.service.AdminService;

import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth/admin")
public class AdminAuthController {

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.password}")
    private String adminPassword;

    private final AdminRepository adminRepository;
    private final AdminService adminService;
    private final PasswordEncoder passwordEncoder;

    public AdminAuthController(
            AdminRepository adminRepository,
            AdminService adminService,
            PasswordEncoder passwordEncoder) {

        this.adminRepository = adminRepository;
        this.adminService = adminService;
        this.passwordEncoder = passwordEncoder;
    }

    // Admin Login
    @PostMapping("/login")
    public ResponseEntity<?> adminLogin(
            @RequestBody Map<String, String> request,
            HttpSession session) {

        String email = request.get("email");
        String password = request.get("password");

        if (email == null || password == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Email and password are required."));
        }

        email = email.trim().toLowerCase();

        boolean validAdmin = false;
        String loggedInEmail = email;

        Admin admin = adminRepository.findByEmail(email).orElse(null);

        if (admin != null) {

            validAdmin = passwordEncoder.matches(
                    password,
                    admin.getPassword()
            );

        } else if (
                adminEmail.equalsIgnoreCase(email)
                        && adminPassword.equals(password)
        ) {

            // Existing default Admin login
            validAdmin = true;
            loggedInEmail = adminEmail;
        }

        if (!validAdmin) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message",
                            "Invalid admin email or password."));
        }

//        session.changeSessionId();

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        loggedInEmail,
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
                );

        SecurityContext securityContext =
                SecurityContextHolder.createEmptyContext();

        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);

        session.setAttribute(
                HttpSessionSecurityContextRepository
                        .SPRING_SECURITY_CONTEXT_KEY,
                securityContext
        );

        session.setAttribute("adminLoggedIn", true);
        session.setAttribute("adminEmail", loggedInEmail);

        return ResponseEntity.ok(
                Map.of("message", "Admin login successful!")
        );
    }

    // Register New Admin
    @PostMapping("/register")
    public ResponseEntity<?> registerAdmin(
            @RequestBody Map<String, String> request) {

        String name = request.get("name");
        String email = request.get("email");
        String password = request.get("password");

        if (name == null || name.isBlank()
                || email == null || email.isBlank()
                || password == null || password.length() < 8) {

            return ResponseEntity.badRequest()
                    .body(Map.of("message",
                            "Enter name, email and a password of at least 8 characters."));
        }

        email = email.trim().toLowerCase();

        if (adminEmail.equalsIgnoreCase(email)) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message",
                            "This email is reserved for the default Admin."));
        }

        Admin newAdmin = new Admin();
        newAdmin.setName(name.trim());
        newAdmin.setEmail(email);
        newAdmin.setPassword(password);

        try {
            adminService.registerAdmin(newAdmin);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message",
                            "New Admin registered successfully!"));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }
    @PostMapping("/logout")
    public ResponseEntity<?> adminLogout(HttpServletRequest request) {

        HttpSession session = request.getSession(false);

        if (session != null) {
            session.invalidate();
        }

        SecurityContextHolder.clearContext();

        return ResponseEntity.ok(
                Map.of("message", "Admin logout successful!")
        );
    }
}