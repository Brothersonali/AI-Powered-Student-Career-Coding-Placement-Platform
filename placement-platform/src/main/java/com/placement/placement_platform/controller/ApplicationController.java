package com.placement.placement_platform.controller;

import com.placement.placement_platform.entity.Application;
import com.placement.placement_platform.repository.ApplicationRepository;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/applications")
public class ApplicationController {

    private final ApplicationRepository applicationRepository;

    public ApplicationController(
            ApplicationRepository applicationRepository) {
        this.applicationRepository = applicationRepository;
    }

    // Check Admin authentication
    private void checkAdmin() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated() ||
                authentication.getAuthorities().stream()
                        .noneMatch(authority ->
                                authority.getAuthority().equals("ROLE_ADMIN"))) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only admin can perform this action."
            );
        }
    }

    // Student applies for a job
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Application applyForJob(
            @RequestBody Application application,
            HttpSession session) {

        Object email = session.getAttribute("studentEmail");

        if (email == null || email.toString().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Please login first."
            );
        }

        application.setId(null);

        // Use logged-in student's email only
        application.setEmail(email.toString());

        application.setStatus("Pending");

        return applicationRepository.save(application);
    }

    // Student views own applications
    @GetMapping("/my")
    public ResponseEntity<?> getMyApplications(HttpSession session) {

        Object email = session.getAttribute("studentEmail");

        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Please login first."));
        }

        List<Application> applications =
                applicationRepository
                        .findByEmailIgnoreCaseOrderByAppliedAtDesc(
                                email.toString()
                        );

        return ResponseEntity.ok(applications);
    }

    // Admin views all applications
    @GetMapping
    public List<Application> getAllApplications() {

        checkAdmin();

        return applicationRepository.findAll();
    }

    // Only Admin can update application status
    @PatchMapping("/{id}/status")
    public Application updateApplicationStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {

        checkAdmin();

        Application application =
                applicationRepository.findById(id)
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Application not found"
                        ));

        String status = body.get("status");

        if (status == null || !Arrays.asList(
                "Pending", "Shortlisted", "Rejected"
        ).contains(status)) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid application status"
            );
        }

        application.setStatus(status);

        return applicationRepository.save(application);
    }

    // Only Admin can fix old application statuses
    @PutMapping("/fix-pending-status")
    public String fixPendingStatuses() {

        checkAdmin();

        List<Application> applications =
                applicationRepository.findAll();

        int updatedCount = 0;

        for (Application application : applications) {

            if (application.getStatus() == null ||
                    application.getStatus().isBlank()) {

                application.setStatus("Pending");

                applicationRepository.save(application);

                updatedCount++;
            }
        }

        return updatedCount +
                " application statuses updated to Pending";
    }
}