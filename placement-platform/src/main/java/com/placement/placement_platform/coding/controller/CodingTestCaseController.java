package com.placement.placement_platform.coding.controller;

import com.placement.placement_platform.coding.entity.CodingTestCase;
import com.placement.placement_platform.coding.service.CodingTestCaseService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coding/questions/{questionId}/test-cases")
public class CodingTestCaseController {

    private final CodingTestCaseService service;

    public CodingTestCaseController(CodingTestCaseService service) {
        this.service = service;
    }

    // Add test case
    @PostMapping
    public CodingTestCase addTestCase(
            @PathVariable Long questionId,
            @RequestBody CodingTestCase testCase) {

        return service.addTestCase(questionId, testCase);
    }

    // Get all test cases of a question
    @GetMapping
    public List<CodingTestCase> getTestCases(
            @PathVariable Long questionId) {

        return service.getTestCases(questionId);
    }

    // Delete test case
    @DeleteMapping("/{id}")
    public String deleteTestCase(@PathVariable Long id) {

        service.deleteTestCase(id);
        return "Test case deleted successfully";
    }
}