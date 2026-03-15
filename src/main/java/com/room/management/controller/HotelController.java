package com.room.management.controller;

import com.room.management.annotation.AuthResource;
import com.room.management.dto.request.CreateHotelRequestDto;
import com.room.management.dto.request.UpdateHotelRequestDto;
import com.room.management.dto.response.ApiResponse;
import com.room.management.dto.response.HotelResponseDto;
import com.room.management.service.HotelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hotels")
@RequiredArgsConstructor
@Tag(name = "Hotel Management", description = "CRUD operations for hotels")
@AuthResource(value = "hotel-management", category = "HOTEL")
public class HotelController {

    private final HotelService hotelService;

    @GetMapping
    @AuthResource(value = "list-hotels", description = "List all hotels")
    @Operation(summary = "List all hotels")
    public ResponseEntity<ApiResponse<List<HotelResponseDto>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(hotelService.getAll()));
    }

    @GetMapping("/{id}")
    @AuthResource(value = "get-hotel", description = "Get hotel by ID")
    @Operation(summary = "Get hotel by ID")
    public ResponseEntity<ApiResponse<HotelResponseDto>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(hotelService.getById(id)));
    }

    @PostMapping
    @AuthResource(value = "create-hotel", description = "Create new hotel")
    @Operation(summary = "Create new hotel")
    public ResponseEntity<ApiResponse<HotelResponseDto>> create(@Valid @RequestBody CreateHotelRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Hotel created successfully", hotelService.create(request)));
    }

    @PutMapping("/{id}")
    @AuthResource(value = "update-hotel", description = "Update hotel")
    @Operation(summary = "Update hotel")
    public ResponseEntity<ApiResponse<HotelResponseDto>> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateHotelRequestDto request) {
        return ResponseEntity.ok(ApiResponse.success("Hotel updated successfully", hotelService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    @AuthResource(value = "delete-hotel", description = "Delete hotel")
    @Operation(summary = "Delete hotel (soft delete)")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        hotelService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Hotel deleted successfully", null));
    }
}
