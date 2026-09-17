package com.scms.repository;

import com.scms.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Repository - handles database operations for Student
// JpaRepository already gives us save, findAll, findById, deleteById etc.
@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
}