package btvn.it211_project.service;

import btvn.it211_project.domain.Course;
import btvn.it211_project.domain.LectureMaterial;
import btvn.it211_project.domain.Role;
import btvn.it211_project.domain.UserAccount;
import btvn.it211_project.dto.request.LectureMaterialUploadRequest;
import btvn.it211_project.exception.ResourceNotFoundException;
import btvn.it211_project.repository.CourseRepository;
import btvn.it211_project.repository.LectureMaterialRepository;
import btvn.it211_project.repository.UserAccountRepository;
import btvn.it211_project.service.impl.LectureMaterialServiceImpl;
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
public class LectureMaterialServiceTest {

    @Mock
    private LectureMaterialRepository materialRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private UserAccountRepository userAccountRepository;

    @InjectMocks
    private LectureMaterialServiceImpl materialService;

    @Test
    public void uploadMaterial_CourseNotFound_ThrowsResourceNotFoundException() {
        Long lecturerId = 2L;
        LectureMaterialUploadRequest request = new LectureMaterialUploadRequest();
        request.setCourseId(999L);
        request.setTitle("Spring Boot Intro");

        UserAccount lecturer = new UserAccount("Lecturer", "l@it211.local", "pass", "123", Role.LECTURER);

        when(userAccountRepository.findById(lecturerId)).thenReturn(Optional.of(lecturer));
        when(courseRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> materialService.uploadMaterial(lecturerId, request));
        verify(materialRepository, never()).save(any(LectureMaterial.class));
    }

    @Test
    public void uploadMaterial_LecturerNotFound_ThrowsResourceNotFoundException() {
        Long lecturerId = 999L;
        LectureMaterialUploadRequest request = new LectureMaterialUploadRequest();
        request.setCourseId(1L);

        when(userAccountRepository.findById(lecturerId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> materialService.uploadMaterial(lecturerId, request));
        verify(materialRepository, never()).save(any(LectureMaterial.class));
    }
}
