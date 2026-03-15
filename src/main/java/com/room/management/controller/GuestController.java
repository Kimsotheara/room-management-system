package com.room.management.controller;

import com.room.management.annotation.AuthResource;
import com.room.management.dto.request.CreateGuestRequestDto;
import com.room.management.dto.request.UpdateGuestRequestDto;
import com.room.management.dto.response.ApiResponse;
import com.room.management.dto.response.GuestResponseDto;
import com.room.management.service.GuestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("/api/guests")
@RequiredArgsConstructor
@Tag(name = "Guest Management", description = "CRUD operations for guests")
@AuthResource(value = "guest-management", category = "GUEST")
public class GuestController {

    private final GuestService guestService;

    @GetMapping
    @AuthResource(value = "list-guests", description = "List all guests")
    @Operation(summary = "List all guests")
    public ResponseEntity<ApiResponse<List<GuestResponseDto>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(guestService.getAll()));
    }

    @GetMapping("/{id}")
    @AuthResource(value = "get-guest", description = "Get guest by ID")
    @Operation(summary = "Get guest by ID")
    public ResponseEntity<ApiResponse<GuestResponseDto>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(guestService.getById(id)));
    }

    @PostMapping
    @AuthResource(value = "create-guest", description = "Create new guest")
    @Operation(summary = "Create new guest")
    public ResponseEntity<ApiResponse<GuestResponseDto>> create(@Valid @RequestBody CreateGuestRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Guest created successfully", guestService.create(request)));
    }

    @PutMapping("/{id}")
    @AuthResource(value = "update-guest", description = "Update guest")
    @Operation(summary = "Update guest")
    public ResponseEntity<ApiResponse<GuestResponseDto>> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateGuestRequestDto request) {
        return ResponseEntity.ok(ApiResponse.success("Guest updated successfully", guestService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    @AuthResource(value = "delete-guest", description = "Delete guest")
    @Operation(summary = "Delete guest (soft delete)")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        guestService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Guest deleted successfully", null));
    }

    @PutMapping(value = "/{id}/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @AuthResource(value = "upload-guest-profile-image", description = "Upload guest profile image")
    @Operation(summary = "Upload profile image", description = "Upload or replace the guest profile image (JPEG, PNG, WEBP, GIF)")
    public ResponseEntity<ApiResponse<GuestResponseDto>> uploadProfileImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) throws IOException {
        String base64Image = Base64.getEncoder().encodeToString(file.getBytes());
        return ResponseEntity.ok(ApiResponse.success("Profile image uploaded successfully",
                guestService.uploadProfileImage(id, base64Image)));
    }

    @DeleteMapping("/{id}/profile-image")
    @AuthResource(value = "delete-guest-profile-image", description = "Delete guest profile image")
    @Operation(summary = "Delete profile image")
    public ResponseEntity<ApiResponse<Void>> deleteProfileImage(@PathVariable Long id) {
        guestService.deleteProfileImage(id);
        return ResponseEntity.ok(ApiResponse.success("Profile image removed successfully", null));
    }
}
