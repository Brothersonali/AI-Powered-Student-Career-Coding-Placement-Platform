package com.placement.placement_platform.coding.repository;

import com.placement.placement_platform.coding.entity.CodingTestCase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CodingTestCaseRepository
        extends JpaRepository<CodingTestCase, Long> {

    List<CodingTestCase> findByQuestionId(Long questionId);
}