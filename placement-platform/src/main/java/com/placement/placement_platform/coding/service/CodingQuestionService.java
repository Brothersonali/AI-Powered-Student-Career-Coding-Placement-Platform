package com.placement.placement_platform.coding.service;

import com.placement.placement_platform.coding.entity.CodingQuestion;
import com.placement.placement_platform.coding.entity.CodingTestCase;
import com.placement.placement_platform.coding.repository.CodingQuestionRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CodingQuestionService {

    private final CodingQuestionRepository repository;

    public CodingQuestionService(CodingQuestionRepository repository) {
        this.repository = repository;
    }

    public CodingQuestion saveQuestion(CodingQuestion question) {

        if (question.getTestCases() != null) {

            for (CodingTestCase testCase : question.getTestCases()) {
                testCase.setQuestion(question);
            }
        }

        return repository.save(question);
    }

    public List<CodingQuestion> getAllQuestions() {
        return repository.findAll();
    }

    public Optional<CodingQuestion> getQuestionById(Long id) {
        return repository.findById(id);
    }

    public void deleteQuestion(Long id) {
        repository.deleteById(id);
    }
}