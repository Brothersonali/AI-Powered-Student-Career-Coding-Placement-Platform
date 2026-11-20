package com.placement.placement_platform.coding.repository;

import com.placement.placement_platform.coding.entity.CodingQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CodingQuestionRepository extends JpaRepository<CodingQuestion, Long> {
}