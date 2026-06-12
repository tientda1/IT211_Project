package btvn.it211_project.controller;

import btvn.it211_project.dto.request.StudentRegistrationRequest;
import btvn.it211_project.dto.response.ApiResponse;
import btvn.it211_project.dto.response.UserResponse;
import btvn.it211_project.service.UserAccountService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StudentRegistrationControllerTest {

    @Mock
    private UserAccountService userAccountService;

    @InjectMocks
    private StudentRegistrationController studentRegistrationController;

    @Test
    public void registerStudent_Success_ReturnsCreatedStatus() {
        StudentRegistrationRequest request = new StudentRegistrationRequest();
        request.setEmail("student@gmail.com");
        request.setFullName("John Doe");
        request.setPassword("password123");

        UserResponse userResponse = UserResponse.builder()
                .id(3L)
                .email("student@gmail.com")
                .fullName("John Doe")
                .build();

        when(userAccountService.registerStudent(any(StudentRegistrationRequest.class))).thenReturn(userResponse);

        ResponseEntity<ApiResponse<UserResponse>> response = studentRegistrationController.register(request);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Student registered successfully", response.getBody().getMessage());
        assertEquals("student@gmail.com", response.getBody().getData().getEmail());
    }
}
