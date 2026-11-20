package com.placement.placement_platform.service;

import com.placement.placement_platform.entity.Job;
import com.placement.placement_platform.repository.JobRepository;
import com.placement.placement_platform.repository.ApplicationRepository;
import com.placement.placement_platform.exception.JobNotFoundException;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class JobService {

    private final JobRepository jobRepository;
    private final ApplicationRepository applicationRepository;

    public JobService(
            JobRepository jobRepository,
            ApplicationRepository applicationRepository) {

        this.jobRepository = jobRepository;
        this.applicationRepository = applicationRepository;
    }

    // Get all jobs
    public List<Job> getAllJobs() {
        return jobRepository.findAll();
    }

    // Get jobs by exact location
    public List<Job> getJobsByLocation(String location) {
        return jobRepository.findByLocation(location);
    }

    // Add new job
    public Job saveJob(Job job) {
        return jobRepository.save(job);
    }

    // Delete job safely
    @Transactional
    public void deleteJob(Long id) {

        if (!jobRepository.existsById(id)) {
            throw new JobNotFoundException(
                    "Job not found with id: " + id
            );
        }

        boolean hasApplications =
                !applicationRepository.findByJobId(id).isEmpty();

        if (hasApplications) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cannot delete this job because applications exist."
            );
        }

        jobRepository.deleteById(id);
    }

    // Get job by ID
    public Job getJobById(Long id) {
        return jobRepository.findById(id)
                .orElseThrow(() ->
                        new JobNotFoundException(
                                "Job not found with id: " + id
                        ));
    }

    // Update job
    public Job updateJob(Job job) {

        if (!jobRepository.existsById(job.getId())) {
            throw new JobNotFoundException(
                    "Job not found with id: " + job.getId()
            );
        }

        return jobRepository.save(job);
    }

    // Search jobs by location
    public List<Job> searchJobsByLocation(String location) {
        return jobRepository.findByLocationContainingIgnoreCase(location);
    }

    // Search jobs by company
    public List<Job> searchJobsByCompany(String companyName) {
        return jobRepository.findByCompanyNameContainingIgnoreCase(companyName);
    }

    // Search jobs by title
    public List<Job> searchJobsByTitle(String title) {
        return jobRepository.findByTitleContainingIgnoreCase(title);
    }

    // Get jobs by minimum salary
    public List<Job> getJobsByMinimumSalary(Double salary) {
        return jobRepository.findBySalaryGreaterThanEqual(salary);
    }

    // Pagination
    public Page<Job> getJobs(Pageable pageable) {
        return jobRepository.findAll(pageable);
    }

    // Get jobs by salary range
    public List<Job> getJobsBySalaryRange(
            Double minSalary, Double maxSalary) {

        return jobRepository.findBySalaryBetween(minSalary, maxSalary);
    }

    // Get jobs by location and minimum salary
    public List<Job> getJobsByLocationAndSalary(
            String location, Double salary) {

        return jobRepository.findByLocationAndSalaryGreaterThanEqual(
                location, salary
        );
    }

    // Search jobs by skills
    public List<Job> searchJobBySkills(String skills) {
        return jobRepository.findBySkillsContainingIgnoreCase(skills);
    }

    // Get all jobs sorted
    public List<Job> getAllJobs(Sort sort) {
        return jobRepository.findAll(sort);
    }

    // Total job count
    public long getJobCount() {
        return jobRepository.count();
    }

    // Job count by company
    public long getJobCountByCompany(String companyName) {
        return jobRepository.countByCompanyNameIgnoreCase(companyName);
    }

    // Delete jobs by company safely
    @Transactional
    public long deleteJobsByCompany(String companyName) {

        List<Job> jobs =
                jobRepository.findByCompanyNameContainingIgnoreCase(companyName);

        if (jobs.isEmpty()) {
            return 0;
        }

        for (Job job : jobs) {

            if (!applicationRepository.findByJobId(job.getId()).isEmpty()) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Cannot delete company jobs because applications exist."
                );
            }
        }

        return jobRepository.deleteByCompanyNameIgnoreCase(companyName);
    }
}