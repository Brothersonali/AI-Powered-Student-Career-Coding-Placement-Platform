package com.placement.placement_platform.repository;

import com.placement.placement_platform.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationRepository
        extends JpaRepository<Application, Long> {
}