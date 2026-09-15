package com.scms.service;

import com.scms.dto.CourseDTO;
import com.scms.entity.Course;
import com.scms.entity.Student;
import com.scms.exception.ResourceNotFoundException;
import com.scms.repository.CourseRepository;
import com.scms.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

// Service layer - business logic for Course + conversion between Entity and DTO
@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private StudentRepository studentRepository;

    // Convert Entity -> DTO
    private CourseDTO toDTO(Course course) {
        return new CourseDTO(course.getId(), course.getCourseName(), course.getCourseCode(), course.getStudent().getId());
    }

    // Create a new course - validates that the student exists first
    public CourseDTO createCourse(CourseDTO dto) {
        Student student = studentRepository.findById(dto.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + dto.getStudentId()));

        Course course = new Course();
        course.setCourseName(dto.getCourseName());
        course.setCourseCode(dto.getCourseCode());
        course.setStudent(student);

        return toDTO(courseRepository.save(course));
    }

    // Get a page of courses instead of the entire table at once
    public Page<CourseDTO> getAllCourses(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return courseRepository.findAll(pageable).map(this::toDTO);
    }

    // Get single course by id
    public CourseDTO getCourseById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
        return toDTO(course);
    }

    // Get all courses belonging to a specific student (not paginated - list is typically small)
    public List<CourseDTO> getCoursesByStudentId(Long studentId) {
        return courseRepository.findByStudentId(studentId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // Update existing course
    public CourseDTO updateCourse(Long id, CourseDTO dto) {
        Course existing = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));

        Student student = studentRepository.findById(dto.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + dto.getStudentId()));

        existing.setCourseName(dto.getCourseName());
        existing.setCourseCode(dto.getCourseCode());
        existing.setStudent(student);

        return toDTO(courseRepository.save(existing));
    }

    // Delete course, throws exception if not found
    public void deleteCourse(Long id) {
        if (!courseRepository.existsById(id)) {
            throw new ResourceNotFoundException("Course not found with id: " + id);
        }
        courseRepository.deleteById(id);
    }
}