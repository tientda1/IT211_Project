package btvn.it211_project.controller;

import btvn.it211_project.dto.request.AssignmentSubmissionRequest;
import btvn.it211_project.dto.response.ApiResponse;
import btvn.it211_project.dto.response.AssignmentSubmissionResponse;
import btvn.it211_project.service.AssignmentSubmissionService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StudentSubmissionControllerTest {

    @Mock
    private AssignmentSubmissionService submissionService;

    @InjectMocks
    private StudentSubmissionController studentSubmissionController;

    @BeforeEach
    public void setup() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(3L, null, List.of(new SimpleGrantedAuthority("ROLE_STUDENT")))
        );
    }

    @AfterEach
    public void teardown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void submitAssignment_Success_ReturnsCreated() {
        AssignmentSubmissionRequest request = new AssignmentSubmissionRequest(1L, "https://github.com/student/repo");

        AssignmentSubmissionResponse response = AssignmentSubmissionResponse.builder()
                .id(1L)
                .assignmentId(1L)
                .studentId(3L)
                .submissionContent("https://github.com/student/repo")
                .submissionDate(LocalDateTime.now())
                .status("SUBMITTED")
                .build();

        when(submissionService.submitAssignment(eq(3L), any(AssignmentSubmissionRequest.class))).thenReturn(response);

        ResponseEntity<ApiResponse<AssignmentSubmissionResponse>> result = studentSubmissionController.submitAssignment(request);

        assertNotNull(result);
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals("Assignment submitted successfully", result.getBody().getMessage());
        assertEquals("https://github.com/student/repo", result.getBody().getData().getSubmissionContent());
    }
}
