package com.placement.placement_platform.coding.service;

import com.placement.placement_platform.coding.entity.CodingQuestion;
import com.placement.placement_platform.coding.entity.CodingTestCase;
import com.placement.placement_platform.coding.repository.CodingQuestionRepository;
import com.placement.placement_platform.coding.repository.CodingTestCaseRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CodingTestCaseService {

    private final CodingTestCaseRepository testCaseRepository;
    private final CodingQuestionRepository questionRepository;

    public CodingTestCaseService(
            CodingTestCaseRepository testCaseRepository,
            CodingQuestionRepository questionRepository) {
        this.testCaseRepository = testCaseRepository;
        this.questionRepository = questionRepository;
    }

    public CodingTestCase addTestCase(
            Long questionId, CodingTestCase testCase) {

        CodingQuestion question = questionRepository
                .findById(questionId)
                .orElseThrow(() ->
                        new RuntimeException("Question not found"));

        testCase.setQuestion(question);

        return testCaseRepository.save(testCase);
    }

    public List<CodingTestCase> getTestCases(Long questionId) {
        return testCaseRepository.findByQuestionId(questionId);
    }

    public void deleteTestCase(Long id) {
        testCaseRepository.deleteById(id);
    }
}