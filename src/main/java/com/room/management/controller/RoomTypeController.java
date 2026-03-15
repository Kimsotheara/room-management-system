package com.room.management.controller;

import com.room.management.annotation.AuthResource;
import com.room.management.dto.request.CreateRoomTypeRequestDto;
import com.room.management.dto.request.UpdateRoomTypeRequestDto;
import com.room.management.dto.response.ApiResponse;
import com.room.management.dto.response.RoomTypeResponseDto;
import com.room.management.service.RoomTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/room-types")
@RequiredArgsConstructor
@Tag(name = "Room Type Management", description = "CRUD operations for room types")
@AuthResource(value = "room-type-management", category = "ROOM_TYPE")
public class RoomTypeController {

    private final RoomTypeService roomTypeService;

    @GetMapping
    @AuthResource(value = "list-room-types", description = "List all room types")
    @Operation(summary = "List all room types")
    public ResponseEntity<ApiResponse<List<RoomTypeResponseDto>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(roomTypeService.getAll()));
    }

    @GetMapping("/{id}")
    @AuthResource(value = "get-room-type", description = "Get room type by ID")
    @Operation(summary = "Get room type by ID")
    public ResponseEntity<ApiResponse<RoomTypeResponseDto>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(roomTypeService.getById(id)));
    }

    @PostMapping
    @AuthResource(value = "create-room-type", description = "Create new room type")
    @Operation(summary = "Create new room type")
    public ResponseEntity<ApiResponse<RoomTypeResponseDto>> create(@Valid @RequestBody CreateRoomTypeRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Room type created successfully", roomTypeService.create(request)));
    }

    @PutMapping("/{id}")
    @AuthResource(value = "update-room-type", description = "Update room type")
    @Operation(summary = "Update room type")
    public ResponseEntity<ApiResponse<RoomTypeResponseDto>> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateRoomTypeRequestDto request) {
        return ResponseEntity.ok(ApiResponse.success("Room type updated successfully", roomTypeService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    @AuthResource(value = "delete-room-type", description = "Delete room type")
    @Operation(summary = "Delete room type (soft delete)")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        roomTypeService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Room type deleted successfully", null));
    }
}
