package btvn.it211_project.controller;

import btvn.it211_project.dto.request.GradingRequest;
import btvn.it211_project.dto.response.ApiResponse;
import btvn.it211_project.dto.response.AssignmentSubmissionResponse;
import btvn.it211_project.service.AssignmentSubmissionService;
import btvn.it211_project.service.LectureMaterialService;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LecturerControllerTest {

    @Mock
    private AssignmentSubmissionService submissionService;

    @Mock
    private LectureMaterialService materialService;

    @InjectMocks
    private LecturerController lecturerController;

    @BeforeEach
    public void setup() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(2L, null, List.of(new SimpleGrantedAuthority("ROLE_LECTURER")))
        );
    }

    @AfterEach
    public void teardown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void gradeSubmission_Success_ReturnsOk() {
        GradingRequest request = new GradingRequest(90, "Good job");

        AssignmentSubmissionResponse response = AssignmentSubmissionResponse.builder()
                .id(1L)
                .score(90)
                .feedback("Good job")
                .status("GRADED")
                .build();

        when(submissionService.gradeSubmission(eq(1L), eq(2L), any(GradingRequest.class))).thenReturn(response);

        ResponseEntity<ApiResponse<AssignmentSubmissionResponse>> result = lecturerController.gradeSubmission(1L, request);

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Submission graded successfully", result.getBody().getMessage());
        assertEquals(90, result.getBody().getData().getScore());
        assertEquals("Good job", result.getBody().getData().getFeedback());
    }
}
