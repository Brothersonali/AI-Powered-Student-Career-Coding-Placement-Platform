package com.placement.placement_platform.service;

import com.placement.placement_platform.dto.RegisterRequest;
import com.placement.placement_platform.entity.Student;
import com.placement.placement_platform.repository.StudentRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;

    public StudentService(
            StudentRepository studentRepository,
            PasswordEncoder passwordEncoder) {
        this.studentRepository = studentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Student register(RegisterRequest request) {

        String email = request.getEmail().trim().toLowerCase();

        if (studentRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException(
                    "This email is already registered."
            );
        }

        Student student = new Student();

        student.setName(request.getName().trim());
        student.setEmail(email);
        student.setPhone(request.getPhone().trim());

        // Password ko hash karke save karna hai.
        student.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        return studentRepository.save(student);
    }
    public Student login(String email, String password) {

        Student student = studentRepository
                .findByEmailIgnoreCase(email.trim())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Invalid email or password."
                        )
                );

        if (!passwordEncoder.matches(
                password, student.getPassword())) {
            throw new IllegalArgumentException(
                    "Invalid email or password."
            );
        }

        return student;
    }
    public Student getStudentByEmail(String email) {
        return studentRepository
                .findByEmailIgnoreCase(email.trim())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Student not found."
                        )
                );
    }
    @Transactional
    public Student updateStudent(Student student){
        return
                studentRepository.save(student);
    }
}