package com.placement.placement_platform.controller;

import com.placement.placement_platform.dto.RegisterRequest;
import com.placement.placement_platform.entity.Student;
import com.placement.placement_platform.service.StudentService;
import com.placement.placement_platform.dto.LoginRequest;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import java.util.Collections;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final StudentService studentService;

    public AuthController(StudentService studentService) {
        this.studentService = studentService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @Valid @RequestBody RegisterRequest request) {

        try {
            Student student = studentService.register(request);

            return ResponseEntity.status(HttpStatus.CREATED).body(
                    Map.of(
                            "message", "Registration successful!",
                            "id", student.getId(),
                            "name", student.getName(),
                            "email", student.getEmail()
                    )
            );

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", e.getMessage())
            );
        }
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest) {

        try {
            Student student = studentService.login(
                    request.getEmail(),
                    request.getPassword()
            );

            // Purana session ho to invalidate karo.
            HttpSession oldSession = httpRequest.getSession(false);
            if (oldSession != null) {
                oldSession.invalidate();
            }

            // Naya session create karo.
            HttpSession session = httpRequest.getSession(true);
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            student.getEmail(),
                            null,
                            Collections.emptyList()
                    );

            SecurityContext securityContext =
                    SecurityContextHolder.createEmptyContext();

            securityContext.setAuthentication(authentication);
            SecurityContextHolder.setContext(securityContext);

            session.setAttribute(
                    HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                    securityContext
            );
            session.setAttribute("studentId", student.getId());
            session.setAttribute("studentEmail", student.getEmail());

            return ResponseEntity.ok(
                    Map.of(
                            "message", "Login successful!",
                            "id", student.getId(),
                            "name", student.getName(),
                            "email", student.getEmail()
                    )
            );

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    Map.of("message", "Invalid email or password.")
            );
        }
    }
}