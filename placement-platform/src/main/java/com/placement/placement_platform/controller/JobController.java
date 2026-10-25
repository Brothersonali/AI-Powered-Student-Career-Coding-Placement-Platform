package com.placement.placement_platform.controller;

import com.placement.placement_platform.entity.Job;
import com.placement.placement_platform.service.JobService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@RestController
@CrossOrigin(origins = "*")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    // Get all jobs
    @GetMapping("/jobs")
    public List<Job> getAllJobs() {
        return jobService.getAllJobs();
    }

    // Get jobs by location
    @GetMapping("/jobs/location/{location}")
    public List<Job> getJobsByLocation(
            @PathVariable String location) {
        return jobService.getJobsByLocation(location);
    }

    // Search jobs by location
    @GetMapping("/jobs/search")
    public List<Job> searchJobsByLocation(
            @RequestParam("location") String location) {
        return jobService.searchJobsByLocation(location);
    }

    // Search jobs by company
    @GetMapping("/jobs/search/company")
    public List<Job> searchJobsByCompany(
            @RequestParam("companyName") String companyName) {
        return jobService.searchJobsByCompany(companyName);
    }
    @GetMapping("/jobs/search/title")
    public List<Job>
    searchJobsByTitle(@RequestParam("title")String title){
        return
                jobService.searchJobsByTitle(title);
    }

    // Get job by ID
    @GetMapping("/jobs/{id}")
    public Job getJobById(@PathVariable Long id) {
        return jobService.getJobById(id);
    }
    @GetMapping("/jobs/search/salary")
    public List<Job> getJobsByMinimumSalary(
            @RequestParam Double salary){
        return jobService.getJobsByMinimumSalary(salary);

    }
    @GetMapping("/jobs/page")
    public Page<Job> getJobWithPagination(
            @RequestParam(defaultValue = "0")
            int page,
            @RequestParam(defaultValue = "3")int size){
        Pageable pageable = PageRequest.of(page, size, Sort.by("salary").descending());
        return jobService.getJobs(pageable);

    }
    @GetMapping("/jobs/search/salary-range")
    public List<Job> getJobsBySalaryRange(
            @RequestParam Double minSalary,
            @RequestParam Double maxSalary){
        return
                jobService.getJobsBySalaryRange(minSalary, maxSalary);
    }
    @GetMapping("/jobs/search/location-salary")
    public List<Job>
    getJobsByLocationAndSalary(
            @RequestParam String location,
            @RequestParam Double salary){
        return
                jobService.getJobsByLocationAndSalary(location,salary);

    }
    @GetMapping("/jobs/search/skills")
    public List<Job> searchJobsBySkills(
            @RequestParam String skills) {

        return
                jobService.searchJobBySkills(skills);
    }
    @GetMapping("/jobs/sort")
    public List<Job> sortJobsBySalary(
            @RequestParam(defaultValue = "desc") String order){
        Sort sort;
        if ((order.equalsIgnoreCase("asc"))){
            sort=
                    Sort.by("salary").ascending();
        }else{
            sort=
                    Sort.by("salary").descending();
        }
        return
                jobService.getAllJobs(sort);

    }

    @GetMapping("/jobs/count")
    public long getJobCount(){
        return jobService.getJobCount();
    }

    @GetMapping("/jobs/count/company")
    public long getJobCountByCompany(
            @RequestParam String companyName){
        return
                jobService.getJobCountByCompany(companyName);
    }








    // Add a new job
    @PostMapping("/jobs")
    public Job saveJob(@Valid @RequestBody Job job) {
        return jobService.saveJob(job);
    }

    // Delete a job
    @DeleteMapping("/jobs/{id}")
    public ResponseEntity<String>
    deleteJob(@PathVariable Long id){
        jobService.deleteJob(id);
        return ResponseEntity.ok("Job deleted successfully");
    }

    // Update an existing job
    @PutMapping("/jobs/{id}")
    public Job updateJob(
            @PathVariable Long id,
        @Valid @RequestBody Job job) {
        job.setId(id);
        return jobService.updateJob(job);
    }
    @DeleteMapping("/jobs/company")
    public long deleteJobsByCompany(
            @RequestParam String
            companyName){
        return
                jobService.deleteJobsByCompany(companyName);

    }

}