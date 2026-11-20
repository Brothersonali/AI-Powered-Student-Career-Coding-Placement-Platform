package com.placement.placement_platform.controller;

import com.placement.placement_platform.entity.Student;
import com.placement.placement_platform.service.StudentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/students")
public class AdminStudentController {

    private final StudentService studentService;

    public AdminStudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    // Get all students
    @GetMapping
    public ResponseEntity<?> getAllStudents() {

        List<Map<String, Object>> students =
                studentService.getAllStudents()
                        .stream()
                        .map(this::studentToMap)
                        .toList();

        return ResponseEntity.ok(students);
    }

    // Get student by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getStudentById(@PathVariable Long id) {

        try {
            Student student = studentService.getStudentById(id);

            return ResponseEntity.ok(studentToMap(student));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404)
                    .body(Map.of("message", "Student not found."));
        }
    }

    // Delete student by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStudent(@PathVariable Long id) {

        try {
            studentService.deleteStudentById(id);

            return ResponseEntity.ok(
                    Map.of("message", "Student deleted successfully.")
            );

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404)
                    .body(Map.of("message", "Student not found."));
        }
    }

    // Convert Student entity into safe response
    private Map<String, Object> studentToMap(Student student) {

        return Map.of(
                "id", student.getId(),
                "name", student.getName(),
                "email", student.getEmail(),
                "phone", student.getPhone() == null ? "" : student.getPhone(),
                "semester", student.getSemester() == null ? "" : student.getSemester(),
                "careerGoal", student.getCareerGoal() == null ? "" : student.getCareerGoal(),
                "skills", student.getSkills() == null ? "" : student.getSkills()
        );
    }
}