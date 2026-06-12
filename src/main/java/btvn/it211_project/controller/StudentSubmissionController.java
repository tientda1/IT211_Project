package btvn.it211_project.controller;

import btvn.it211_project.dto.request.AssignmentSubmissionRequest;
import btvn.it211_project.dto.response.ApiResponse;
import btvn.it211_project.dto.response.AssignmentSubmissionResponse;
import btvn.it211_project.service.AssignmentSubmissionService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/submissions")
public class StudentSubmissionController {

    private final AssignmentSubmissionService submissionService;

    public StudentSubmissionController(AssignmentSubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    /**
     * FR-07: Submit assignment/project (GitHub link or File) - Student
     */
    @PostMapping("/submit")
    public ResponseEntity<ApiResponse<AssignmentSubmissionResponse>> submitAssignment(
            @Valid @RequestBody AssignmentSubmissionRequest request) {

        Long studentId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
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
    public ResponseEntity<ApiResponse<List<AssignmentSubmissionResponse>>> getMySubmissions() {
        Long studentId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
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
            @RequestParam(defaultValue = "10") int size) {

        Long studentId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
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
}
