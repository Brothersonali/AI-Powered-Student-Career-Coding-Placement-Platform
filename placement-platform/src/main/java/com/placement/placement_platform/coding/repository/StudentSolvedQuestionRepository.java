package com.placement.placement_platform.coding.repository;

import com.placement.placement_platform.coding.entity.StudentSolvedQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentSolvedQuestionRepository
        extends JpaRepository<StudentSolvedQuestion, Long> {

    boolean existsByStudentIdAndQuestionId(
            Long studentId,
            Long questionId
    );

    List<StudentSolvedQuestion> findByStudentId(Long studentId);
}