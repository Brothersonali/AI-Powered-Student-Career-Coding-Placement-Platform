package com.placement.placement_platform.service;

import com.placement.placement_platform.entity.Job;
import com.placement.placement_platform.repository.JobRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.placement.placement_platform.exception.JobNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class JobService {

    private final JobRepository jobRepository;

    public JobService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    public List<Job> getAllJobs() {
        return jobRepository.findAll();
    }
    public List<Job> getJobsByLocation(String location){
        return jobRepository.findByLocation(location);

    }
    public Job saveJob(Job job){
        return
                jobRepository.save(job);
    }
    public void deleteJob(Long id){

        if(!
        jobRepository.existsById(id)){
            throw new JobNotFoundException(
                    "Job not found with id: " + id
            );
        }
        jobRepository.deleteById(id);
    }
    public Job getJobById(Long id){
        return jobRepository.findById(id).orElseThrow(()->
                new
                        JobNotFoundException("Job not found with id: "+ id));
    }
    public Job updateJob(Job job){
        if(!
        jobRepository.existsById(job.getId())){
            throw new JobNotFoundException("Job not found with id: " + job.getId()
            );

        }
        return jobRepository.save(job);


    }
    public List<Job> searchJobsByLocation(String location){
        return jobRepository.findByLocationContainingIgnoreCase(location);
    }
    public List<Job> searchJobsByCompany(String companyName){
        return jobRepository.findByCompanyNameContainingIgnoreCase(companyName);
    }
    public List<Job> searchJobsByTitle(String title){
        return
                jobRepository.findByTitleContainingIgnoreCase(title);
    }
    public List<Job> getJobsByMinimumSalary(Double salary){
        return jobRepository.findBySalaryGreaterThanEqual(salary);
    }
    public Page<Job> getJobs(Pageable pageable){
        return jobRepository.findAll(pageable);
    }
    public List<Job> getJobsBySalaryRange(Double minSalary, Double maxSalary){
        return
                jobRepository.findBySalaryBetween(minSalary, maxSalary);
    }
    public List<Job> getJobsByLocationAndSalary(
            String location, Double salary){
        return jobRepository.findByLocationAndSalaryGreaterThanEqual(location, salary);
    }
    public List<Job> searchJobBySkills(String skills){
        return
                jobRepository.findBySkillsContainingIgnoreCase(skills);
    }
    public List<Job> getAllJobs(Sort sort){
        return
                jobRepository.findAll(sort);

    }
    public long getJobCount(){
        return jobRepository.count();
    }

    public long
    getJobCountByCompany(String companyName){
        return
                jobRepository.countByCompanyNameIgnoreCase(companyName);
    }
    @Transactional
    public long
    deleteJobsByCompany(String companyName){
        return
                jobRepository.deleteByCompanyNameIgnoreCase(companyName);
    }

}