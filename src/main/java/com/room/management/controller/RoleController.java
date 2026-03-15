package com.room.management.controller;

import com.room.management.annotation.AuthResource;
import com.room.management.dto.request.CreateRoleRequestDto;
import com.room.management.dto.request.UpdateRoleRequestDto;
import com.room.management.dto.response.ApiResponse;
import com.room.management.dto.response.RoleResponseDto;
import com.room.management.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@Tag(name = "Role Management", description = "CRUD operations for roles")
@AuthResource(value = "role-management", category = "ROLE")
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    @AuthResource(value = "list-roles", description = "List all roles")
    @Operation(summary = "List all roles")
    public ResponseEntity<ApiResponse<List<RoleResponseDto>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(roleService.getAll()));
    }

    @GetMapping("/{id}")
    @AuthResource(value = "get-role", description = "Get role by ID")
    @Operation(summary = "Get role by ID")
    public ResponseEntity<ApiResponse<RoleResponseDto>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(roleService.getById(id)));
    }

    @PostMapping
    @AuthResource(value = "create-role", description = "Create new role")
    @Operation(summary = "Create new role")
    public ResponseEntity<ApiResponse<RoleResponseDto>> create(@Valid @RequestBody CreateRoleRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Role created successfully", roleService.create(request)));
    }

    @PutMapping("/{id}")
    @AuthResource(value = "update-role", description = "Update role")
    @Operation(summary = "Update role")
    public ResponseEntity<ApiResponse<RoleResponseDto>> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateRoleRequestDto request) {
        return ResponseEntity.ok(ApiResponse.success("Role updated successfully", roleService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    @AuthResource(value = "delete-role", description = "Delete role")
    @Operation(summary = "Delete role (soft delete)")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        roleService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Role deleted successfully", null));
    }
}
