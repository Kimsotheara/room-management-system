package com.room.management.controller;

import com.room.management.annotation.AuthResource;
import com.room.management.dto.request.CreatePromotionRequestDto;
import com.room.management.dto.request.UpdatePromotionRequestDto;
import com.room.management.dto.response.ApiResponse;
import com.room.management.dto.response.PromotionResponseDto;
import com.room.management.service.PromotionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/promotions")
@RequiredArgsConstructor
@Tag(name = "Promotion Management", description = "CRUD operations for Promotion")
@AuthResource(value = "promotion-management", category = "PROMOTION")
public class PromotionController {

    private final PromotionService promotionService;

    @GetMapping
    @AuthResource(value = "list-promotions", description = "List all promotions")
    @Operation(summary = "List all promotions")
    public ResponseEntity<ApiResponse<List<PromotionResponseDto>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(promotionService.getAllPromotions()));
    }

    @GetMapping("/{id}")
    @AuthResource(value = "get-promotion", description = "Get promotion by ID")
    @Operation(summary = "Get promotion by ID")
    public ResponseEntity<ApiResponse<PromotionResponseDto>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(promotionService.getById(id)));
    }

    @PostMapping
    @AuthResource(value = "create-promotion", description = "Create new promotion")
    @Operation(summary = "Create new promotion")
    public ResponseEntity<ApiResponse<PromotionResponseDto>> create(
            @Valid @RequestBody CreatePromotionRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Promotion created successfully", promotionService.create(request)));
    }

    @PutMapping("/{id}")
    @AuthResource(value = "update-promotion", description = "Update promotion")
    @Operation(summary = "Update promotion")
    public ResponseEntity<ApiResponse<PromotionResponseDto>> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePromotionRequestDto request) {
        return ResponseEntity.ok(ApiResponse.success("Promotion updated successfully", promotionService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    @AuthResource(value = "delete-promotion", description = "Delete promotion")
    @Operation(summary = "Delete promotion (soft delete)")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        promotionService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Promotion deleted successfully", null));
    }
}
