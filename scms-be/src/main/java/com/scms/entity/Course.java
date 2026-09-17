package com.scms.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Entity class - maps to 'courses' table. Many courses can belong to one student.
@Entity
@Table(name = "courses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String courseName;

    @Column(nullable = false)
    private String courseCode;

    // Many-to-One: many courses can belong to one student.
    // JoinColumn creates 'student_id' foreign key column in 'courses' table.
    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;
}