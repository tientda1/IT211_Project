package btvn.it211_project.controller;

import btvn.it211_project.domain.Role;
import btvn.it211_project.dto.request.UserUpsertRequest;
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
public class AdminUserControllerTest {

    @Mock
    private UserAccountService userAccountService;

    @InjectMocks
    private AdminUserController adminUserController;

    @Test
    public void createUser_Success_ReturnsCreatedStatus() {
        UserUpsertRequest request = new UserUpsertRequest();
        request.setEmail("lecturer@it211.local");
        request.setFullName("Demo Lecturer");
        request.setPassword("Lecturer@123");
        request.setRole(Role.LECTURER);

        UserResponse userResponse = UserResponse.builder()
                .id(2L)
                .email("lecturer@it211.local")
                .fullName("Demo Lecturer")
                .role(Role.LECTURER)
                .build();

        when(userAccountService.createUser(any(UserUpsertRequest.class))).thenReturn(userResponse);

        ResponseEntity<ApiResponse<UserResponse>> response = adminUserController.createUser(request);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("User created successfully", response.getBody().getMessage());
        assertEquals("lecturer@it211.local", response.getBody().getData().getEmail());
    }
}
