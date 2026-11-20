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

    // Get Student Profile
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

    // Update Student Profile
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

            // Update Name
            if (updates.containsKey("name")) {
                String name = (String) updates.get("name");

                if (name == null || name.isBlank()) {
                    return ResponseEntity.badRequest().body(
                            Map.of("message", "Name cannot be empty.")
                    );
                }

                student.setName(name.trim());
            }

            // Update Phone
            if (updates.containsKey("phone")) {
                String phone = (String) updates.get("phone");

                if (phone == null ||
                        !phone.matches("^[0-9]{10}$")) {

                    return ResponseEntity.badRequest().body(
                            Map.of("message",
                                    "Phone number must be exactly 10 digits.")
                    );
                }

                student.setPhone(phone);
            }

            // Update Semester
            if (updates.containsKey("semester")) {
                Object semesterValue = updates.get("semester");

                if (semesterValue == null ||
                        semesterValue.toString().isBlank()) {
                    student.setSemester(null);
                } else {
                    int semester = Integer.parseInt(
                            semesterValue.toString()
                    );

                    if (semester < 1 || semester > 8) {
                        return ResponseEntity.badRequest().body(
                                Map.of("message",
                                        "Semester must be between 1 and 8.")
                        );
                    }

                    student.setSemester(semester);
                }
            }

            // Update Career Goal
            if (updates.containsKey("careerGoal")) {
                student.setCareerGoal(
                        (String) updates.get("careerGoal")
                );
            }

            // Update Skills
            if (updates.containsKey("skills")) {
                student.setSkills(
                        (String) updates.get("skills")
                );
            }

            Student savedStudent =
                    studentService.updateStudent(student);

            return ResponseEntity.ok(
                    Map.of(
                            "message", "Profile updated successfully.",
                            "profile", studentToMap(savedStudent)
                    )
            );

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }

    // Convert Student Entity to Safe Response
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