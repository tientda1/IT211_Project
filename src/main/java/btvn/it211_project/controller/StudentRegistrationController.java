package btvn.it211_project.controller;

import btvn.it211_project.dto.request.StudentRegistrationRequest;
import btvn.it211_project.dto.response.ApiResponse;
import btvn.it211_project.dto.response.UserResponse;
import btvn.it211_project.service.UserAccountService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/students")
public class StudentRegistrationController {

    private final UserAccountService userAccountService;

    public StudentRegistrationController(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(@Valid @RequestBody StudentRegistrationRequest request) {
        UserResponse response = userAccountService.registerStudent(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<UserResponse>builder()
                        .message("Student registered successfully")
                        .data(response)
                        .build());
    }
}