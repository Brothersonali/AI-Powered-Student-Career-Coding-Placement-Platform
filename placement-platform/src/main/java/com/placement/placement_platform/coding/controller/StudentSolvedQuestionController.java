package com.placement.placement_platform.coding.controller;

import com.placement.placement_platform.coding.entity.StudentSolvedQuestion;
import com.placement.placement_platform.coding.service.StudentSolvedQuestionService;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/coding")
public class StudentSolvedQuestionController {

    private final StudentSolvedQuestionService solvedService;

    public StudentSolvedQuestionController(
            StudentSolvedQuestionService solvedService) {
        this.solvedService = solvedService;
    }

    @GetMapping("/solved")
    public ResponseEntity<?> getSolvedQuestions(HttpSession session) {

        Object studentIdObject = session.getAttribute("studentId");

        if (studentIdObject == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Please login first."));
        }

        Long studentId = Long.valueOf(studentIdObject.toString());

        List<Long> solvedQuestionIds =
                solvedService.getSolvedQuestions(studentId)
                        .stream()
                        .map(StudentSolvedQuestion::getQuestionId)
                        .collect(Collectors.toList());

        return ResponseEntity.ok(solvedQuestionIds);
    }
}