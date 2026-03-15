package com.room.management.controller;

import com.room.management.annotation.AuthResource;
import com.room.management.dto.request.CreateRoomRequestDto;
import com.room.management.dto.request.ImageDataRequestDto;
import com.room.management.dto.request.UpdateRoomRequestDto;
import com.room.management.dto.response.ApiResponse;
import com.room.management.dto.response.RoomResponseDto;
import com.room.management.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
@Tag(name = "Room Management", description = "CRUD operations for rooms")
@AuthResource(value = "room-management", category = "ROOM")
public class RoomController {

    private final RoomService roomService;

    @GetMapping
    @AuthResource(value = "list-rooms", description = "List all rooms")
    @Operation(summary = "List all rooms")
    public ResponseEntity<ApiResponse<List<RoomResponseDto>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(roomService.getAll()));
    }

    @GetMapping("/{id}")
    @AuthResource(value = "get-room", description = "Get room by ID")
    @Operation(summary = "Get room by ID")
    public ResponseEntity<ApiResponse<RoomResponseDto>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(roomService.getById(id)));
    }

    @PostMapping
    @AuthResource(value = "create-room", description = "Create new room")
    @Operation(summary = "Create new room")
    public ResponseEntity<ApiResponse<RoomResponseDto>> create(@Valid @RequestBody CreateRoomRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Room created successfully", roomService.create(request)));
    }

    @PutMapping("/{id}")
    @AuthResource(value = "update-room", description = "Update room")
    @Operation(summary = "Update room")
    public ResponseEntity<ApiResponse<RoomResponseDto>> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateRoomRequestDto request) {
        return ResponseEntity.ok(ApiResponse.success("Room updated successfully", roomService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    @AuthResource(value = "delete-room", description = "Delete room")
    @Operation(summary = "Delete room (soft delete)")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        roomService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Room deleted successfully", null));
    }

    @PostMapping("/{id}/images")
    @AuthResource(value = "upload-room-image", description = "Add image(s) to room")
    @Operation(summary = "Add room image(s)", description = "Send one or more Base64 encoded images in the 'images' list")
    public ResponseEntity<ApiResponse<RoomResponseDto>> addImages(
            @PathVariable Long id,
            @Valid @RequestBody ImageDataRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Image(s) added successfully", roomService.addImages(id, request.getImages())));
    }

    @DeleteMapping("/{id}/images/{imageId}")
    @AuthResource(value = "delete-room-image", description = "Delete image from room")
    @Operation(summary = "Delete room image")
    public ResponseEntity<ApiResponse<Void>> deleteImage(
            @PathVariable Long id,
            @PathVariable Long imageId) {
        roomService.deleteImage(id, imageId);
        return ResponseEntity.ok(ApiResponse.success("Image deleted successfully", null));
    }
}
