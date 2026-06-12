package btvn.it211_project.service.impl;

import btvn.it211_project.domain.Course;
import btvn.it211_project.dto.request.CourseUpsertRequest;
import btvn.it211_project.dto.response.CourseResponse;
import btvn.it211_project.exception.DuplicateResourceException;
import btvn.it211_project.exception.ResourceNotFoundException;
import btvn.it211_project.repository.CourseRepository;
import btvn.it211_project.repository.EnrollmentRepository;
import btvn.it211_project.service.CourseService;

import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;

    public CourseServiceImpl(CourseRepository courseRepository, EnrollmentRepository enrollmentRepository) {
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    @Override
    public CourseResponse createCourse(CourseUpsertRequest request) {
        if (courseRepository.existsByCode(request.getCode())) {
            throw new DuplicateResourceException("Course code already exists");
        }

        Course course = new Course(
                request.getCode(),
                request.getName(),
                request.getDescription(),
                request.getMaxStudents());
        if (request.getActive() != null) {
            course.setActive(request.getActive());
        }

        return toResponse(courseRepository.save(course));
    }

    @Override
    public CourseResponse updateCourse(Long id, CourseUpsertRequest request) {
        Course course = getRequiredCourse(id);
        if (!Objects.equals(course.getCode(), request.getCode()) && courseRepository.existsByCode(request.getCode())) {
            throw new DuplicateResourceException("Course code already exists");
        }

        course.setCode(request.getCode());
        course.setName(request.getName());
        course.setDescription(request.getDescription());
        course.setMaxStudents(request.getMaxStudents());
        if (request.getActive() != null) {
            course.setActive(request.getActive());
        }

        return toResponse(courseRepository.save(course));
    }

    @Override
    public void deactivateCourse(Long id) {
        Course course = getRequiredCourse(id);
        course.setActive(false);
        courseRepository.save(course);
    }

    @Override
    @Transactional(readOnly = true)
    public CourseResponse getCourseById(Long id) {
        return toResponse(getRequiredCourse(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CourseResponse> searchCourses(String keyword, Pageable pageable) {
        Page<Course> pageResult;
        if (keyword != null && !keyword.isBlank()) {
            pageResult = courseRepository.findByNameContainingIgnoreCaseOrCodeContainingIgnoreCase(keyword, keyword, pageable);
        } else {
            pageResult = courseRepository.findAll(pageable);
        }

        return pageResult.map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Course getRequiredCourse(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found: " + id));
    }

    private CourseResponse toResponse(Course course) {
        long enrolledCount = enrollmentRepository.countByCourseId(course.getId());

        return CourseResponse.builder()
                .id(course.getId())
                .code(course.getCode())
                .name(course.getName())
                .description(course.getDescription())
                .maxStudents(course.getMaxStudents())
                .active(course.isActive())
                .enrolledCount(enrolledCount)
                .createdAt(course.getCreatedAt())
                .build();
    }
}