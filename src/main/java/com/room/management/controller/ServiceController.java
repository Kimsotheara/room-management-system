package com.room.management.controller;

import com.room.management.annotation.AuthResource;
import com.room.management.dto.request.*;
import com.room.management.dto.response.ApiResponse;
import com.room.management.dto.response.RoomTypeResponseDto;
import com.room.management.dto.response.ServiceResponseDto;
import com.room.management.service.ServicesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
@Tag(name = "Service Management", description = "CRUD operations for Service")
@AuthResource(value = "service-management", category = "SERVICE")
public class ServiceController {

    private final ServicesService service;


    @GetMapping
    @AuthResource(value = "list-service", description = "List all service")
    @Operation(summary = "List all service")
    public ResponseEntity<ApiResponse<List<ServiceResponseDto>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(service.getAllServices()));
    }

    @GetMapping("/{id}")
    @AuthResource(value = "get-service", description = "Get service by ID")
    @Operation(summary = "Get service by ID")
    public ResponseEntity<ApiResponse<ServiceResponseDto>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(service.getById(id)));
    }

    @PostMapping
    @AuthResource(value = "create-service", description = "Create new service")
    @Operation(summary = "Create new service")
    public ResponseEntity<ApiResponse<ServiceResponseDto>> create(@Valid @RequestBody CreateServiceRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("service created successfully", service.create(request)));
    }

    @PutMapping("/{id}")
    @AuthResource(value = "update-service", description = "Update service")
    @Operation(summary = "Update service")
    public ResponseEntity<ApiResponse<ServiceResponseDto>> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateServiceRequestDto request) {
        return ResponseEntity.ok(ApiResponse.success("service updated successfully", service.update(id, request)));
    }

    @DeleteMapping("/{id}")
    @AuthResource(value = "delete-service", description = "Delete service")
    @Operation(summary = "Delete service (soft delete)")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.success("service deleted successfully", null));
    }

}
