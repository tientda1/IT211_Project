package btvn.it211_project.service;

import btvn.it211_project.domain.Course;
import btvn.it211_project.domain.Enrollment;
import btvn.it211_project.domain.Role;
import btvn.it211_project.domain.UserAccount;
import btvn.it211_project.dto.request.EnrollmentRequest;
import btvn.it211_project.exception.BusinessRuleException;
import btvn.it211_project.repository.EnrollmentRepository;
import btvn.it211_project.repository.UserAccountRepository;
import btvn.it211_project.service.impl.EnrollmentServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EnrollmentServiceTest {

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private UserAccountRepository userAccountRepository;

    @Mock
    private CourseService courseService;

    @InjectMocks
    private EnrollmentServiceImpl enrollmentService;

    @Test
    public void enroll_UserIsNotStudent_ThrowsBusinessRuleException() {
        Long courseId = 1L;
        EnrollmentRequest request = new EnrollmentRequest();
        request.setStudentId(2L);

        UserAccount lecturer = new UserAccount("Lecturer", "l@it211.local", "pass", "123", Role.LECTURER);
        when(userAccountRepository.findById(2L)).thenReturn(Optional.of(lecturer));

        assertThrows(BusinessRuleException.class, () -> enrollmentService.enroll(courseId, request));
        verify(enrollmentRepository, never()).save(any(Enrollment.class));
    }

    @Test
    public void enroll_CourseIsFull_ThrowsBusinessRuleException() {
        Long courseId = 1L;
        EnrollmentRequest request = new EnrollmentRequest();
        request.setStudentId(3L);

        UserAccount student = new UserAccount("Student", "s@it211.local", "pass", "123", Role.STUDENT);
        student.setId(3L);
        
        Course course = new Course("IT101", "Java Fundamentals", "Desc", 3);
        course.setId(courseId);
        course.setActive(true);

        when(userAccountRepository.findById(3L)).thenReturn(Optional.of(student));
        when(courseService.getRequiredCourse(courseId)).thenReturn(course);
        when(enrollmentRepository.existsByStudentAndCourse(student, course)).thenReturn(false);
        when(enrollmentRepository.countByCourseId(courseId)).thenReturn(3L); // Max is 3, current is 3 -> Full

        assertThrows(BusinessRuleException.class, () -> enrollmentService.enroll(courseId, request));
        verify(enrollmentRepository, never()).save(any(Enrollment.class));
    }
}
