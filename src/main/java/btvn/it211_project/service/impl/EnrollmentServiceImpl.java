package btvn.it211_project.service.impl;

import btvn.it211_project.domain.Course;
import btvn.it211_project.domain.Enrollment;
import btvn.it211_project.domain.Role;
import btvn.it211_project.domain.UserAccount;
import btvn.it211_project.dto.request.EnrollmentRequest;
import btvn.it211_project.dto.response.EnrollmentResponse;
import btvn.it211_project.exception.BusinessRuleException;
import btvn.it211_project.exception.DuplicateResourceException;
import btvn.it211_project.exception.ResourceNotFoundException;
import btvn.it211_project.repository.EnrollmentRepository;
import btvn.it211_project.repository.UserAccountRepository;
import btvn.it211_project.service.CourseService;
import btvn.it211_project.service.EnrollmentService;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final UserAccountRepository userAccountRepository;
    private final CourseService courseService;

    public EnrollmentServiceImpl(EnrollmentRepository enrollmentRepository,
                                 UserAccountRepository userAccountRepository,
                                 CourseService courseService) {
        this.enrollmentRepository = enrollmentRepository;
        this.userAccountRepository = userAccountRepository;
        this.courseService = courseService;
    }

    @Override
    public EnrollmentResponse enroll(Long courseId, EnrollmentRequest request) {
        UserAccount student = userAccountRepository.findById(request.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found: " + request.getStudentId()));

        if (student.getRole() != Role.STUDENT) {
            throw new BusinessRuleException("Only student accounts can enroll in a course");
        }

        Course course = courseService.getRequiredCourse(courseId);

        if (!course.isActive()) {
            throw new BusinessRuleException("Course is inactive");
        }

        if (enrollmentRepository.existsByStudentAndCourse(student, course)) {
            throw new DuplicateResourceException("Student already enrolled in this course");
        }

        long currentEnrollmentCount = enrollmentRepository.countByCourseId(course.getId());
        if (currentEnrollmentCount >= course.getMaxStudents()) {
            throw new BusinessRuleException("Course is full");
        }

        Enrollment enrollment = new Enrollment(student, course);
        return toResponse(enrollmentRepository.save(enrollment));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnrollmentResponse> getEnrollmentsForStudent(Long studentId) {
        return enrollmentRepository.findByStudentId(studentId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private EnrollmentResponse toResponse(Enrollment enrollment) {
        return EnrollmentResponse.builder()
                .id(enrollment.getId())
                .studentId(enrollment.getStudent().getId())
                .studentName(enrollment.getStudent().getFullName())
                .courseId(enrollment.getCourse().getId())
                .courseCode(enrollment.getCourse().getCode())
                .courseName(enrollment.getCourse().getName())
                .status(enrollment.getStatus())
                .enrolledAt(enrollment.getEnrolledAt())
                .build();
    }
}