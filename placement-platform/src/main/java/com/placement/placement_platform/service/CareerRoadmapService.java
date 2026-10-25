package com.placement.placement_platform.service;

import com.placement.placement_platform.entity.CareerRoadmap;
import com.placement.placement_platform.repository.CareerRoadmapRepository;
import org.springframework.stereotype.Service;

@Service
public class CareerRoadmapService {
    private final CareerRoadmapRepository roadmapRepository;

    public CareerRoadmapService(CareerRoadmapRepository roadmapRepository){
        this.roadmapRepository = roadmapRepository;
    }
    public CareerRoadmap generateRoadmap(String careerGoal) {
        CareerRoadmap roadmap = new CareerRoadmap();

        roadmap.setCareerGoal(careerGoal);
        String steps;

        if (careerGoal.equalsIgnoreCase("Java Developer")) {
            steps = "1. Core Java\n"
                    + "2. OOP concepts\n"
                    + "3. DSA practice\n"
                    + "4. SQL and PostgreSQL\n"
                    + "5. Spring Boot\n"
                    + "6. Build projects\n"
                    + "7. Interview preparation";
        } else if
        (careerGoal.equalsIgnoreCase("Web Developer")) {
            steps = "1. HTML\n"
                    + "2. CSS\n"
                    + "3. JavaScript\n"
                    + "4. React basics\n"
                    + "5. Build projects\n"
                    + "6. Interview preparation";
        } else {
            throw new
                    IllegalArgumentException("Unsupported career goal. Choose Java Developer or Web Developer."
            );

        }
        roadmap.setRoadmap(steps);

        return
                roadmapRepository.save(roadmap);
    }

}
