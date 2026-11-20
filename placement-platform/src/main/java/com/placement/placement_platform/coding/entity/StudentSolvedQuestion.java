package com.placement.placement_platform.coding.entity;

import jakarta.persistence.*;

@Entity
@Table(
        name = "student_solved_questions",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"student_id", "question_id"}
                )
        }
)
public class StudentSolvedQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(name = "question_id", nullable = false)
    private Long questionId;

    public Long getId() {
        return id;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }
}