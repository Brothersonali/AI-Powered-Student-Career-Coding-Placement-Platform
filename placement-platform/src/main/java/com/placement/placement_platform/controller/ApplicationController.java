package com.placement.placement_platform.controller;

import com.placement.placement_platform.entity.Application;
import com.placement.placement_platform.repository.ApplicationRepository;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
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

    // Apply for a job
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Application applyForJob(
            @Valid @RequestBody Application application) {

        application.setId(null);
        application.setStatus("Pending");

        return applicationRepository.save(application);
    }

    // Get all applications
    @GetMapping
    public List<Application> getAllApplications() {
        return applicationRepository.findAll();
    }

    // Update application status
    @PatchMapping("/{id}/status")
    public Application updateApplicationStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {

        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Application not found"));

        String status = body.get("status");

        if (status == null || !Arrays.asList(
                "Pending", "Shortlisted", "Rejected"
        ).contains(status)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid application status");
        }

        application.setStatus(status);

        return applicationRepository.save(application);
    }

    // Fix old applications with an empty status
    @PutMapping("/fix-pending-status")
    public String fixPendingStatuses() {

        List<Application> applications =
                applicationRepository.findAll();

        int updatedCount = 0;

        for (Application application : applications) {
            if (application.getStatus() == null
                    || application.getStatus().isBlank()) {

                application.setStatus("Pending");
                applicationRepository.save(application);
                updatedCount++;
            }
        }

        return updatedCount +
                " application statuses updated to Pending";
    }
}