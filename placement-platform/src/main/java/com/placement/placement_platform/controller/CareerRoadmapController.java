package com.placement.placement_platform.controller;

import com.placement.placement_platform.entity.CareerRoadmap;
import com.placement.placement_platform.service.CareerRoadmapService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/career-roadmap")
public class CareerRoadmapController {

    private final CareerRoadmapService roadmapService;

    public CareerRoadmapController(CareerRoadmapService roadmapService) {
        this.roadmapService = roadmapService;
    }

    @PostMapping
    public CareerRoadmap generateRoadmap(
            @RequestParam String careerGoal) {
        return roadmapService.generateRoadmap(careerGoal);
    }
}