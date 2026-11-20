package com.placement.placement_platform.coding.controller;

import com.placement.placement_platform.coding.dto.CodeSubmissionRequest;
import com.placement.placement_platform.coding.dto.CodeSubmissionResponse;
import com.placement.placement_platform.coding.service.CodeExecutionService;
import com.placement.placement_platform.coding.service.StudentSolvedQuestionService;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/coding")
public class CodeExecutionController {

    private final CodeExecutionService executionService;
    private final StudentSolvedQuestionService solvedService;

    public CodeExecutionController(
            CodeExecutionService executionService,
            StudentSolvedQuestionService solvedService) {

        this.executionService = executionService;
        this.solvedService = solvedService;
    }

    @PostMapping("/execute")
    public ResponseEntity<CodeSubmissionResponse> executeCode(
            @RequestBody CodeSubmissionRequest request,
            HttpSession session) {

        Object studentIdObject = session.getAttribute("studentId");

        if (studentIdObject == null) {
            return ResponseEntity.status(401).build();
        }

        Long studentId = Long.valueOf(
                studentIdObject.toString()
        );

        CodeSubmissionResponse response =
                executionService.executeCode(request, studentId);

        return ResponseEntity.ok(response);
    }
}