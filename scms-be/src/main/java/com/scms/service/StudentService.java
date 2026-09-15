package com.scms.service;

import com.scms.dto.StudentDTO;
import com.scms.entity.Student;
import com.scms.exception.ResourceNotFoundException;
import com.scms.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

// Service layer - business logic + conversion between Entity and DTO
@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    // Convert Entity -> DTO
    private StudentDTO toDTO(Student student) {
        return new StudentDTO(student.getId(), student.getName(), student.getEmail(), student.getAge());
    }

    // Convert DTO -> Entity
    private Student toEntity(StudentDTO dto) {
        Student student = new Student();
        student.setId(dto.getId());
        student.setName(dto.getName());
        student.setEmail(dto.getEmail());
        student.setAge(dto.getAge());
        return student;
    }

    // Create a new student
    public StudentDTO createStudent(StudentDTO dto) {
        Student saved = studentRepository.save(toEntity(dto));
        return toDTO(saved);
    }

    // Get a page of students instead of the entire table at once.
    // Pageable bundles page number + size + (optionally) sorting into one object.
    public Page<StudentDTO> getAllStudents(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return studentRepository.findAll(pageable).map(this::toDTO);
    }

    // Get single student by id, throws exception if not found
    public StudentDTO getStudentById(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
        return toDTO(student);
    }

    // Update existing student, throws exception if not found
    public StudentDTO updateStudent(Long id, StudentDTO dto) {
        Student existing = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
        existing.setName(dto.getName());
        existing.setEmail(dto.getEmail());
        existing.setAge(dto.getAge());
        return toDTO(studentRepository.save(existing));
    }

    // Delete student, throws exception if not found
    public void deleteStudent(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Student not found with id: " + id);
        }
        studentRepository.deleteById(id);
    }
}