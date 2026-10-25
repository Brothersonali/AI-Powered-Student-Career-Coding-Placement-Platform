package com.placement.placement_platform.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class CareerRoadmap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String careerGoal;
    private String roadmap;
    public Long getId(){
        return id;
    }
    public void setId(Long id){
        this.id = id;
    }
    public String getCareerGoal(){
        return careerGoal;
    }
    public void setCareerGoal(String careerGoal){
        this.careerGoal = careerGoal;
    }
    public String getRoadmap(){
        return roadmap;
    }
    public void setRoadmap(String roadmap){
        this.roadmap = roadmap;
    }

}
