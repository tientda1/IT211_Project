package btvn.it211_project.controller;

import btvn.it211_project.dto.request.GradingRequest;
import btvn.it211_project.dto.request.LectureMaterialUploadRequest;
import btvn.it211_project.dto.response.ApiResponse;
import btvn.it211_project.dto.response.AssignmentSubmissionResponse;
import btvn.it211_project.dto.response.LectureMaterialResponse;
import btvn.it211_project.service.AssignmentSubmissionService;
import btvn.it211_project.service.LectureMaterialService;
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
@RequestMapping("/api/v1/lecturer")
public class LecturerController {

    private final AssignmentSubmissionService submissionService;
    private final LectureMaterialService materialService;
    private final JwtTokenProvider jwtTokenProvider;

    public LecturerController(
            AssignmentSubmissionService submissionService,
            LectureMaterialService materialService,
            JwtTokenProvider jwtTokenProvider) {
        this.submissionService = submissionService;
        this.materialService = materialService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * FR-08: Grade submissions & provide feedback
     */
    @PostMapping("/grade/{submissionId}")
    public ResponseEntity<ApiResponse<AssignmentSubmissionResponse>> gradeSubmission(
            @PathVariable Long submissionId,
            @Valid @RequestBody GradingRequest request,
            HttpServletRequest httpRequest) {

        Long lecturerId = extractUserIdFromToken(httpRequest);
        if (lecturerId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.<AssignmentSubmissionResponse>builder()
                            .message("Invalid or missing token")
                            .build());
        }

        AssignmentSubmissionResponse response = submissionService.gradeSubmission(submissionId, lecturerId, request);

        return ResponseEntity.ok(ApiResponse.<AssignmentSubmissionResponse>builder()
                .message("Submission graded successfully")
                .data(response)
                .build());
    }

    /**
     * Get submissions for an assignment
     */
    @GetMapping("/assignments/{assignmentId}/submissions")
    public ResponseEntity<ApiResponse<List<AssignmentSubmissionResponse>>> getAssignmentSubmissions(
            @PathVariable Long assignmentId) {

        List<AssignmentSubmissionResponse> submissions = submissionService.getSubmissionsByAssignment(assignmentId);

        return ResponseEntity.ok(ApiResponse.<List<AssignmentSubmissionResponse>>builder()
                .message("Submissions retrieved successfully")
                .data(submissions)
                .build());
    }

    /**
     * Get paginated submissions for an assignment
     */
    @GetMapping("/assignments/{assignmentId}/submissions/paged")
    public ResponseEntity<ApiResponse<Page<AssignmentSubmissionResponse>>> getAssignmentSubmissionsPaged(
            @PathVariable Long assignmentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<AssignmentSubmissionResponse> submissions = submissionService.getSubmissionsByAssignmentPaged(assignmentId, pageable);

        return ResponseEntity.ok(ApiResponse.<Page<AssignmentSubmissionResponse>>builder()
                .message("Submissions retrieved successfully")
                .data(submissions)
                .build());
    }

    /**
     * Get pending submissions for an assignment
     */
    @GetMapping("/assignments/{assignmentId}/submissions/pending")
    public ResponseEntity<ApiResponse<List<AssignmentSubmissionResponse>>> getPendingSubmissions(
            @PathVariable Long assignmentId) {

        List<AssignmentSubmissionResponse> submissions = submissionService.getPendingSubmissions(assignmentId);

        return ResponseEntity.ok(ApiResponse.<List<AssignmentSubmissionResponse>>builder()
                .message("Pending submissions retrieved successfully")
                .data(submissions)
                .build());
    }

    /**
     * FR-09: Upload lecture materials
     */
    @PostMapping("/materials/upload")
    public ResponseEntity<ApiResponse<LectureMaterialResponse>> uploadMaterial(
            @Valid @RequestBody LectureMaterialUploadRequest request,
            HttpServletRequest httpRequest) {

        Long lecturerId = extractUserIdFromToken(httpRequest);
        if (lecturerId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.<LectureMaterialResponse>builder()
                            .message("Invalid or missing token")
                            .build());
        }

        LectureMaterialResponse response = materialService.uploadMaterial(lecturerId, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<LectureMaterialResponse>builder()
                        .message("Material uploaded successfully")
                        .data(response)
                        .build());
    }

    /**
     * Get lecture materials for a course
     */
    @GetMapping("/courses/{courseId}/materials")
    public ResponseEntity<ApiResponse<List<LectureMaterialResponse>>> getCourseMaterials(
            @PathVariable Long courseId) {

        List<LectureMaterialResponse> materials = materialService.getMaterialsByCourse(courseId);

        return ResponseEntity.ok(ApiResponse.<List<LectureMaterialResponse>>builder()
                .message("Materials retrieved successfully")
                .data(materials)
                .build());
    }

    /**
     * Get paginated lecture materials for a course
     */
    @GetMapping("/courses/{courseId}/materials/paged")
    public ResponseEntity<ApiResponse<Page<LectureMaterialResponse>>> getCourseMaterialsPaged(
            @PathVariable Long courseId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<LectureMaterialResponse> materials = materialService.getMaterialsByCoursePaged(courseId, pageable);

        return ResponseEntity.ok(ApiResponse.<Page<LectureMaterialResponse>>builder()
                .message("Materials retrieved successfully")
                .data(materials)
                .build());
    }

    /**
     * Get specific material
     */
    @GetMapping("/materials/{id}")
    public ResponseEntity<ApiResponse<LectureMaterialResponse>> getMaterial(@PathVariable Long id) {
        LectureMaterialResponse material = materialService.getMaterialById(id);

        return ResponseEntity.ok(ApiResponse.<LectureMaterialResponse>builder()
                .message("Material retrieved successfully")
                .data(material)
                .build());
    }

    /**
     * Update material
     */
    @PutMapping("/materials/{id}")
    public ResponseEntity<ApiResponse<LectureMaterialResponse>> updateMaterial(
            @PathVariable Long id,
            @Valid @RequestBody LectureMaterialUploadRequest request) {

        LectureMaterialResponse response = materialService.updateMaterial(id, request);

        return ResponseEntity.ok(ApiResponse.<LectureMaterialResponse>builder()
                .message("Material updated successfully")
                .data(response)
                .build());
    }

    /**
     * Deactivate material
     */
    @DeleteMapping("/materials/{id}")
    public ResponseEntity<ApiResponse<Void>> deactivateMaterial(@PathVariable Long id) {
        materialService.deactivateMaterial(id);

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .message("Material deactivated successfully")
                .build());
    }

    /**
     * Get my uploaded materials
     */
    @GetMapping("/materials/my-materials")
    public ResponseEntity<ApiResponse<List<LectureMaterialResponse>>> getMyMaterials(
            HttpServletRequest httpRequest) {

        Long lecturerId = extractUserIdFromToken(httpRequest);
        if (lecturerId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.<List<LectureMaterialResponse>>builder()
                            .message("Invalid or missing token")
                            .build());
        }

        List<LectureMaterialResponse> materials = materialService.getMaterialsByLecturer(lecturerId);

        return ResponseEntity.ok(ApiResponse.<List<LectureMaterialResponse>>builder()
                .message("Materials retrieved successfully")
                .data(materials)
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
