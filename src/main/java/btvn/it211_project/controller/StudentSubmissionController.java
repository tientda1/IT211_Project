package btvn.it211_project.controller;

import btvn.it211_project.dto.request.AssignmentSubmissionRequest;
import btvn.it211_project.dto.response.ApiResponse;
import btvn.it211_project.dto.response.AssignmentSubmissionResponse;
import btvn.it211_project.service.AssignmentSubmissionService;
import btvn.it211_project.util.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/submissions")
public class StudentSubmissionController {

    private final AssignmentSubmissionService submissionService;
    private final JwtTokenProvider jwtTokenProvider;

    public StudentSubmissionController(AssignmentSubmissionService submissionService, JwtTokenProvider jwtTokenProvider) {
        this.submissionService = submissionService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * FR-07: Submit assignment/project (GitHub link or File) - Student
     */
    @PostMapping("/submit")
    public ResponseEntity<ApiResponse<AssignmentSubmissionResponse>> submitAssignment(
            @Valid @RequestBody AssignmentSubmissionRequest request,
            HttpServletRequest httpRequest) {

        Long studentId = extractUserIdFromToken(httpRequest);
        if (studentId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.<AssignmentSubmissionResponse>builder()
                            .message("Invalid or missing token")
                            .build());
        }

        AssignmentSubmissionResponse response = submissionService.submitAssignment(studentId, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<AssignmentSubmissionResponse>builder()
                        .message("Assignment submitted successfully")
                        .data(response)
                        .build());
    }

    /**
     * Get student's submissions
     */
    @GetMapping("/my-submissions")
    public ResponseEntity<ApiResponse<List<AssignmentSubmissionResponse>>> getMySubmissions(
            HttpServletRequest httpRequest) {

        Long studentId = extractUserIdFromToken(httpRequest);
        if (studentId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.<List<AssignmentSubmissionResponse>>builder()
                            .message("Invalid or missing token")
                            .build());
        }

        List<AssignmentSubmissionResponse> submissions = submissionService.getSubmissionsByStudent(studentId);

        return ResponseEntity.ok(ApiResponse.<List<AssignmentSubmissionResponse>>builder()
                .message("Submissions retrieved successfully")
                .data(submissions)
                .build());
    }

    /**
     * Get paginated submissions by student
     */
    @GetMapping("/my-submissions/paged")
    public ResponseEntity<ApiResponse<Page<AssignmentSubmissionResponse>>> getMySubmissionsPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest httpRequest) {

        Long studentId = extractUserIdFromToken(httpRequest);
        if (studentId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.<Page<AssignmentSubmissionResponse>>builder()
                            .message("Invalid or missing token")
                            .build());
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<AssignmentSubmissionResponse> submissions = submissionService.getSubmissionsByStudentPaged(studentId, pageable);

        return ResponseEntity.ok(ApiResponse.<Page<AssignmentSubmissionResponse>>builder()
                .message("Submissions retrieved successfully")
                .data(submissions)
                .build());
    }

    /**
     * Get specific submission
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AssignmentSubmissionResponse>> getSubmission(@PathVariable Long id) {
        AssignmentSubmissionResponse submission = submissionService.getSubmissionById(id);

        return ResponseEntity.ok(ApiResponse.<AssignmentSubmissionResponse>builder()
                .message("Submission retrieved successfully")
                .data(submission)
                .build());
    }

    private Long extractUserIdFromToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if (jwtTokenProvider.isTokenValid(token)) {
                return jwtTokenProvider.extractUserId(token);
            }
        }
        return null;
    }
}
