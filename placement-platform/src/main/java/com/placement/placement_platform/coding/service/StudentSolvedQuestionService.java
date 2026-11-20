package com.placement.placement_platform.coding.service;

import com.placement.placement_platform.coding.entity.StudentSolvedQuestion;
import com.placement.placement_platform.coding.repository.StudentSolvedQuestionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentSolvedQuestionService {

    private final StudentSolvedQuestionRepository repository;

    public StudentSolvedQuestionService(
            StudentSolvedQuestionRepository repository) {
        this.repository = repository;
    }

    // Mark question as solved
    public void markAsSolved(Long studentId, Long questionId) {

        boolean alreadySolved =
                repository.existsByStudentIdAndQuestionId(
                        studentId, questionId
                );

        if (!alreadySolved) {
            StudentSolvedQuestion solvedQuestion =
                    new StudentSolvedQuestion();

            solvedQuestion.setStudentId(studentId);
            solvedQuestion.setQuestionId(questionId);

            repository.save(solvedQuestion);
        }
    }

    // Get all solved questions of a student
    public List<StudentSolvedQuestion> getSolvedQuestions(
            Long studentId) {

        return repository.findByStudentId(studentId);
    }
}