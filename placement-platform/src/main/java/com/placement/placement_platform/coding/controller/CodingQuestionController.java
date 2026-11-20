package com.placement.placement_platform.coding.controller;

import com.placement.placement_platform.coding.entity.CodingQuestion;
import com.placement.placement_platform.coding.service.CodingQuestionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coding/questions")
@CrossOrigin
public class CodingQuestionController {

    private final CodingQuestionService service;

    public CodingQuestionController(CodingQuestionService service) {
        this.service = service;
    }

    // Add new coding question
    @PostMapping
    public CodingQuestion addQuestion(@RequestBody CodingQuestion question) {
        return service.saveQuestion(question);
    }

    // Get all coding questions
    @GetMapping
    public List<CodingQuestion> getAllQuestions() {
        return service.getAllQuestions();
    }

    // Get question by ID
    @GetMapping("/{id}")
    public CodingQuestion getQuestionById(@PathVariable Long id) {
        return service.getQuestionById(id).orElse(null);
    }

    // Delete question
    @DeleteMapping("/{id}")
    public String deleteQuestion(@PathVariable Long id) {
        service.deleteQuestion(id);
        return "Coding question deleted successfully";
    }
}