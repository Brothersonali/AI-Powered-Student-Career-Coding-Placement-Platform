package com.placement.placement_platform.repository;

import com.placement.placement_platform.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {
    List<Job> findByLocation(String location);
    List<Job> findByLocationContainingIgnoreCase(String location);
    List<Job> findByCompanyNameContainingIgnoreCase(String companyName);
    List<Job> findByTitleContainingIgnoreCase(String title);
    List<Job> findBySalaryGreaterThanEqual(Double salary);
    List<Job> findBySalaryBetween(Double minSalary, Double maxSalary);
    List<Job> findByLocationAndSalaryGreaterThanEqual(String location, Double salary);
    List<Job> findBySkillsContainingIgnoreCase(String skills);
    long countByCompanyNameIgnoreCase(String companyName);
    long deleteByCompanyNameIgnoreCase(String companyName);

}