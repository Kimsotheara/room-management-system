package com.room.management.controller;

import com.room.management.annotation.AuthResource;
import com.room.management.dto.request.LoginRequestDto;
import com.room.management.dto.request.RefreshTokenRequestDto;
import com.room.management.dto.response.ApiResponse;
import com.room.management.dto.response.CurrentUserResponseDto;
import com.room.management.dto.response.LoginResponseDto;
import com.room.management.entity.auth.Role;
import com.room.management.entity.auth.User;
import com.room.management.security.context.SecurityContext;
import com.room.management.service.auth.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication and token management")
@AuthResource(value = "auth", isCoreResource = true, category = "AUTH")
public class AuthController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    @AuthResource(value = "login", description = "User login", isPublic = true, category = "AUTH")
    @Operation(summary = "Login", description = "Authenticate user and receive JWT tokens")
    public ResponseEntity<ApiResponse<LoginResponseDto>> login(@Valid @RequestBody LoginRequestDto request) {
        return ResponseEntity.ok(ApiResponse.success("Login successful", authenticationService.login(request)));
    }

    @PostMapping("/refresh")
    @AuthResource(value = "refresh-token", description = "Refresh access token", isPublic = true, category = "AUTH")
    @Operation(summary = "Refresh Token", description = "Obtain new access token using refresh token")
    public ResponseEntity<ApiResponse<LoginResponseDto>> refreshToken(
            @Valid @RequestBody RefreshTokenRequestDto request) {
        return ResponseEntity.ok(ApiResponse.success("Token refreshed successfully", authenticationService.refreshToken(request)));
    }

    @PostMapping("/logout")
    @AuthResource(value = "logout", description = "User logout", isPublic = true, category = "AUTH")
    @Operation(summary = "Logout", description = "Revoke all tokens for the authenticated user")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request) {
        String token = extractBearerToken(request);
        if (StringUtils.hasText(token)) {
            authenticationService.logout(token);
        }
        return ResponseEntity.ok(ApiResponse.success("Logged out successfully", null));
    }

    @GetMapping("/me")
    @AuthResource(value = "current-user", description = "Get current user information", category = "AUTH")
    @Operation(summary = "Get Current User", description = "Retrieve authenticated user's profile and permissions")
    public ResponseEntity<ApiResponse<CurrentUserResponseDto>> getCurrentUser() {
        User user = SecurityContext.getCurrentUser();

        CurrentUserResponseDto response = CurrentUserResponseDto.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .isActive(user.getIsActive())
                .roles(user.getActiveRoles().stream()
                        .map(Role::getRoleName)
                        .collect(Collectors.toSet()))
                .permissionCodes(SecurityContext.getCurrentUserPermissions())
                .build();

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    private String extractBearerToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}
