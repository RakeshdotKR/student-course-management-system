package com.scms.repository;

import com.scms.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// Repository - handles database operations for Course
@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    // Custom query method - Spring Data JPA auto-generates the SQL from method name
    List<Course> findByStudentId(Long studentId);
}