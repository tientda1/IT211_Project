package btvn.it211_project.controller;

import btvn.it211_project.dto.request.CourseUpsertRequest;
import btvn.it211_project.dto.response.ApiResponse;
import btvn.it211_project.dto.response.CourseResponse;
import btvn.it211_project.service.CourseService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/courses")
public class AdminCourseController {

    private final CourseService courseService;

    public AdminCourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<CourseResponse>>> searchCourses(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<CourseResponse> result = courseService.searchCourses(keyword, pageable);
        return ResponseEntity.ok(ApiResponse.<Page<CourseResponse>>builder()
                .message("Courses fetched successfully")
                .data(result)
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CourseResponse>> getCourse(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.<CourseResponse>builder()
                .message("Course fetched successfully")
                .data(courseService.getCourseById(id))
                .build());
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CourseResponse>> createCourse(@Valid @RequestBody CourseUpsertRequest request) {
        CourseResponse response = courseService.createCourse(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<CourseResponse>builder()
                        .message("Course created successfully")
                        .data(response)
                        .build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CourseResponse>> updateCourse(@PathVariable Long id,
                                                                    @Valid @RequestBody CourseUpsertRequest request) {
        return ResponseEntity.ok(ApiResponse.<CourseResponse>builder()
                .message("Course updated successfully")
                .data(courseService.updateCourse(id, request))
                .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivateCourse(@PathVariable Long id) {
        courseService.deactivateCourse(id);
        return ResponseEntity.noContent().build();
    }
}