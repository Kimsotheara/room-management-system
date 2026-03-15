package com.room.management.controller;

import com.room.management.annotation.AuthResource;
import com.room.management.dto.request.AssignRoleRequestDto;
import com.room.management.dto.request.CreateUserRequestDto;
import com.room.management.dto.request.UpdateUserRequestDto;
import com.room.management.dto.response.ApiResponse;
import com.room.management.dto.response.UserResponseDto;
import com.room.management.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "CRUD operations for users")
@AuthResource(value = "user-management", category = "USER")
public class UserController {

    private final UserService userService;

    @GetMapping
    @AuthResource(value = "list-users", description = "List all users")
    @Operation(summary = "List all users")
    public ResponseEntity<ApiResponse<List<UserResponseDto>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(userService.getAll()));
    }

    @GetMapping("/{id}")
    @AuthResource(value = "get-user", description = "Get user by ID")
    @Operation(summary = "Get user by ID")
    public ResponseEntity<ApiResponse<UserResponseDto>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(userService.getById(id)));
    }

    @PostMapping
    @AuthResource(value = "create-user", description = "Create new user")
    @Operation(summary = "Create new user")
    public ResponseEntity<ApiResponse<UserResponseDto>> create(@Valid @RequestBody CreateUserRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("User created successfully", userService.create(request)));
    }

    @PutMapping("/{id}")
    @AuthResource(value = "update-user", description = "Update user")
    @Operation(summary = "Update user")
    public ResponseEntity<ApiResponse<UserResponseDto>> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequestDto request) {
        return ResponseEntity.ok(ApiResponse.success("User updated successfully", userService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    @AuthResource(value = "delete-user", description = "Delete user")
    @Operation(summary = "Delete user (soft delete)")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("User deleted successfully", null));
    }

    @PostMapping("/{id}/roles")
    @AuthResource(value = "assign-user-role", description = "Assign role to user")
    @Operation(summary = "Assign role to user")
    public ResponseEntity<ApiResponse<UserResponseDto>> assignRole(
            @PathVariable Long id,
            @Valid @RequestBody AssignRoleRequestDto request) {
        return ResponseEntity.ok(ApiResponse.success("Role assigned successfully", userService.assignRole(id, request)));
    }

    @DeleteMapping("/{id}/roles/{roleId}")
    @AuthResource(value = "remove-user-role", description = "Remove role from user")
    @Operation(summary = "Remove role from user")
    public ResponseEntity<ApiResponse<UserResponseDto>> removeRole(
            @PathVariable Long id,
            @PathVariable Long roleId) {
        return ResponseEntity.ok(ApiResponse.success("Role removed successfully", userService.removeRole(id, roleId)));
    }
}
