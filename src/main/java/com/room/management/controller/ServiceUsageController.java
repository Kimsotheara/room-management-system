package com.room.management.controller;

import com.room.management.annotation.AuthResource;
import com.room.management.dto.request.AddServiceUsageRequestDto;
import com.room.management.dto.response.ApiResponse;
import com.room.management.dto.response.ServiceUsageResponseDto;
import com.room.management.service.ServiceUsageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/service-usages")
@RequiredArgsConstructor
@Tag(name = "Service Usage Management", description = "Manage services consumed by guests during their stay")
@AuthResource(value = "service-usage-management", category = "SERVICE_USAGE")
public class ServiceUsageController {

    private final ServiceUsageService serviceUsageService;

    @GetMapping("/reservation/{reservationId}")
    @AuthResource(value = "list-service-usages", description = "List all service usages for a reservation")
    @Operation(summary = "List service usages by reservation")
    public ResponseEntity<ApiResponse<List<ServiceUsageResponseDto>>> getByReservationId(
            @PathVariable Long reservationId) {
        return ResponseEntity.ok(ApiResponse.success(serviceUsageService.getByReservationId(reservationId)));
    }

    @GetMapping("/{id}")
    @AuthResource(value = "get-service-usage", description = "Get service usage by ID")
    @Operation(summary = "Get service usage by ID")
    public ResponseEntity<ApiResponse<ServiceUsageResponseDto>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(serviceUsageService.getById(id)));
    }

    @PostMapping
    @AuthResource(value = "add-service-usage", description = "Record a service consumed by a guest")
    @Operation(summary = "Add service usage", description = "Records a service (e.g. laundry, food) for a CHECKED_IN reservation and updates the total amount.")
    public ResponseEntity<ApiResponse<ServiceUsageResponseDto>> add(
            @Valid @RequestBody AddServiceUsageRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Service usage recorded successfully", serviceUsageService.add(request)));
    }

    @DeleteMapping("/{id}")
    @AuthResource(value = "delete-service-usage", description = "Remove a service usage and reverse the reservation total")
    @Operation(summary = "Delete service usage", description = "Soft deletes a service usage and subtracts its amount from the reservation total.")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        serviceUsageService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Service usage deleted successfully", null));
    }
}
