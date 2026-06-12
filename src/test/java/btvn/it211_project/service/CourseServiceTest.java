package btvn.it211_project.service;

import btvn.it211_project.domain.Course;
import btvn.it211_project.dto.request.CourseUpsertRequest;
import btvn.it211_project.dto.response.CourseResponse;
import btvn.it211_project.exception.DuplicateResourceException;
import btvn.it211_project.repository.CourseRepository;
import btvn.it211_project.repository.EnrollmentRepository;
import btvn.it211_project.service.impl.CourseServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @InjectMocks
    private CourseServiceImpl courseService;

    @Test
    public void createCourse_DuplicateCode_ThrowsDuplicateResourceException() {
        CourseUpsertRequest request = new CourseUpsertRequest();
        request.setCode("IT101");
        request.setName("Java Programming");
        request.setMaxStudents(30);

        when(courseRepository.existsByCode("IT101")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> courseService.createCourse(request));
        verify(courseRepository, never()).save(any(Course.class));
    }

    @Test
    public void createCourse_ValidRequest_Success() {
        CourseUpsertRequest request = new CourseUpsertRequest();
        request.setCode("IT103");
        request.setName("Software Engineering");
        request.setMaxStudents(40);
        request.setActive(true);

        Course savedCourse = new Course("IT103", "Software Engineering", "Description", 40);

        when(courseRepository.existsByCode("IT103")).thenReturn(false);
        when(courseRepository.save(any(Course.class))).thenReturn(savedCourse);
        when(enrollmentRepository.countByCourseId(any())).thenReturn(0L);

        CourseResponse response = courseService.createCourse(request);

        assertNotNull(response);
        assertEquals("IT103", response.getCode());
        assertEquals("Software Engineering", response.getName());
        assertEquals(40, response.getMaxStudents());
        verify(courseRepository, times(1)).save(any(Course.class));
    }
}
