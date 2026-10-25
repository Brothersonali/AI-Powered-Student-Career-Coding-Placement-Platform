package com.placement.placement_platform.controller;

import com.placement.placement_platform.entity.Student;
import com.placement.placement_platform.service.StudentService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/email")
    public ResponseEntity<?> getStudentByEmail(
            @RequestParam String email,
            HttpSession session) {

        Object loggedInStudentId = session.getAttribute("studentId");

        if (loggedInStudentId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Please login first."));
        }

        try {
            Student student = studentService.getStudentByEmail(email);

            if (!student.getId().equals(loggedInStudentId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message",
                                "You cannot view another student's profile."));
            }

            return ResponseEntity.ok(studentToMap(student));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Student not found."));
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateStudent(
            @RequestParam String email,
            @RequestBody Map<String, Object> updates,
            HttpSession session) {

        Object loggedInStudentId = session.getAttribute("studentId");

        if (loggedInStudentId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Please login first."));
        }

        try {
            Student student = studentService.getStudentByEmail(email);

            if (!student.getId().equals(loggedInStudentId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message",
                                "You cannot update another student's profile."));
            }

            if (updates.containsKey("phone")) {
                student.setPhone((String) updates.get("phone"));
            }

            if (updates.containsKey("semester")) {
                Object semesterValue = updates.get("semester");

                if (semesterValue == null ||
                        semesterValue.toString().isBlank()) {
                    student.setSemester(null);
                } else {
                    student.setSemester(
                            Integer.parseInt(semesterValue.toString())
                    );
                }
            }

            if (updates.containsKey("careerGoal")) {
                student.setCareerGoal(
                        (String) updates.get("careerGoal")
                );
            }

            if (updates.containsKey("skills")) {
                student.setSkills((String) updates.get("skills"));
            }

            Student savedStudent = studentService.updateStudent(student);

            return ResponseEntity.ok(
                    Map.of(
                            "message", "Profile updated successfully.",
                            "profile", studentToMap(savedStudent)
                    )
            );

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        }
    }

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