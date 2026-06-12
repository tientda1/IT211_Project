package btvn.it211_project.controller;

import btvn.it211_project.dto.request.EnrollmentRequest;
import btvn.it211_project.dto.response.ApiResponse;
import btvn.it211_project.dto.response.EnrollmentResponse;
import btvn.it211_project.service.EnrollmentService;
import jakarta.validation.Valid;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/student/courses")
public class StudentEnrollmentController {

    private final EnrollmentService enrollmentService;

    public StudentEnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @PostMapping("/{courseId}/enrollments")
    public ResponseEntity<ApiResponse<EnrollmentResponse>> enroll(@PathVariable Long courseId,
                                                                  @Valid @RequestBody EnrollmentRequest request) {
        EnrollmentResponse response = enrollmentService.enroll(courseId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<EnrollmentResponse>builder()
                        .message("Enrollment created successfully")
                        .data(response)
                        .build());
    }

    @GetMapping("/enrollments")
    public ResponseEntity<ApiResponse<List<EnrollmentResponse>>> myEnrollments(@RequestParam Long studentId) {
        return ResponseEntity.ok(ApiResponse.<List<EnrollmentResponse>>builder()
                .message("Enrollments fetched successfully")
                .data(enrollmentService.getEnrollmentsForStudent(studentId))
                .build());
    }
}