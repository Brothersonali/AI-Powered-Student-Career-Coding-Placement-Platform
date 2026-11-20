package com.placement.placement_platform.repository;

import com.placement.placement_platform.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ApplicationRepository
        extends JpaRepository<Application, Long> {

    List<Application> findByEmailIgnoreCaseOrderByAppliedAtDesc(
            String email
    );

    List<Application> findByJobId(Long jobId);
}