package btvn.it211_project.controller;

import btvn.it211_project.domain.UserAccount;
import btvn.it211_project.dto.request.ChangePasswordRequest;
import btvn.it211_project.dto.request.ForgotPasswordRequest;
import btvn.it211_project.dto.request.LoginRequest;
import btvn.it211_project.dto.request.RefreshTokenRequest;
import btvn.it211_project.dto.response.ApiResponse;
import btvn.it211_project.dto.response.LoginResponse;
import btvn.it211_project.dto.response.TokenRefreshResponse;
import btvn.it211_project.dto.response.UserResponse;
import btvn.it211_project.service.UserAccountService;
import btvn.it211_project.util.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserAccountService userAccountService;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthController(UserAccountService userAccountService, JwtTokenProvider jwtTokenProvider) {
        this.userAccountService = userAccountService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * FR-01: Login system (JWT Token allocation)
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        UserAccount user = userAccountService.findByEmailAndPassword(request.getEmail(), request.getPassword());

        String accessToken = jwtTokenProvider.generateToken(user.getId(), user.getEmail(), user.getRole().name());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId(), user.getEmail());

        // Save refresh token to database
        userAccountService.updateRefreshToken(user.getId(), refreshToken);

        LoginResponse loginResponse = LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(24 * 60 * 60L) // 24 hours
                .user(toUserResponse(user))
                .build();

        return ResponseEntity.ok(ApiResponse.<LoginResponse>builder()
                .message("Login successful")
                .data(loginResponse)
                .build());
    }

    /**
     * FR-02: Token rotation (Refresh Token)
     */
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenRefreshResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        if (!jwtTokenProvider.isTokenValid(request.getRefreshToken())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.<TokenRefreshResponse>builder()
                            .message("Invalid refresh token")
                            .build());
        }

        Long userId = jwtTokenProvider.extractUserId(request.getRefreshToken());
        String email = jwtTokenProvider.extractEmail(request.getRefreshToken());

        UserAccount user = userAccountService.getRequiredUser(userId);

        // Verify the refresh token matches the one in database
        if (user.getRefreshToken() == null || !user.getRefreshToken().equals(request.getRefreshToken())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.<TokenRefreshResponse>builder()
                            .message("Refresh token mismatch")
                            .build());
        }

        String newAccessToken = jwtTokenProvider.generateToken(user.getId(), user.getEmail(), user.getRole().name());
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(user.getId(), user.getEmail());

        // Save new refresh token
        userAccountService.updateRefreshToken(user.getId(), newRefreshToken);

        TokenRefreshResponse response = TokenRefreshResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .expiresIn(24 * 60 * 60L) // 24 hours
                .build();

        return ResponseEntity.ok(ApiResponse.<TokenRefreshResponse>builder()
                .message("Token refreshed successfully")
                .data(response)
                .build());
    }

    /**
     * FR-03: Logout (Revoke Token)
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout() {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Revoke the refresh token
        userAccountService.revokeToken(userId);

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .message("Logout successful")
                .build());
    }

    /**
     * FR-10: Change password
     */
    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @Valid @RequestBody ChangePasswordRequest request) {

        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Verify passwords match
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.<Void>builder()
                            .message("New password and confirmation password do not match")
                            .build());
        }

        userAccountService.changePassword(userId, request.getCurrentPassword(), request.getNewPassword());

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .message("Password changed successfully")
                .build());
    }

    /**
     * FR-10: Forgot password
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        // In a real application, you would generate a password reset token and send it via email
        // For now, we'll set a temporary password
        String temporaryPassword = generateTemporaryPassword();
        userAccountService.resetPassword(request.getEmail(), temporaryPassword);

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .message("Password reset link sent to email. Temporary password: " + temporaryPassword)
                .build());
    }

    private UserResponse toUserResponse(UserAccount user) {
        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .active(user.isActive())
                .createdAt(user.getCreatedAt())
                .build();
    }

    private String generateTemporaryPassword() {
        return "TEMP" + System.currentTimeMillis();
    }
}
