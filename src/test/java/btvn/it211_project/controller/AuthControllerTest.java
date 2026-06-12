package btvn.it211_project.controller;

import btvn.it211_project.domain.Role;
import btvn.it211_project.domain.UserAccount;
import btvn.it211_project.dto.request.LoginRequest;
import btvn.it211_project.dto.response.ApiResponse;
import btvn.it211_project.dto.response.LoginResponse;
import btvn.it211_project.service.UserAccountService;
import btvn.it211_project.util.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    @Mock
    private UserAccountService userAccountService;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthController authController;

    @Test
    public void login_Success_ReturnsTokens() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("student@it211.local");
        loginRequest.setPassword("Student@123");

        UserAccount user = new UserAccount("Demo Student", "student@it211.local", "encodedPassword", "123", Role.STUDENT);
        user.setId(3L);

        when(userAccountService.findByEmailAndPassword("student@it211.local", "Student@123")).thenReturn(user);
        when(jwtTokenProvider.generateToken(3L, "student@it211.local", "STUDENT")).thenReturn("mockAccessToken");
        when(jwtTokenProvider.generateRefreshToken(3L, "student@it211.local")).thenReturn("mockRefreshToken");

        ResponseEntity<ApiResponse<LoginResponse>> response = authController.login(loginRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Login successful", response.getBody().getMessage());
        assertEquals("mockAccessToken", response.getBody().getData().getAccessToken());
        assertEquals("mockRefreshToken", response.getBody().getData().getRefreshToken());
    }
}
