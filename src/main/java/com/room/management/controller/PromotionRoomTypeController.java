package com.room.management.controller;

import com.room.management.annotation.AuthResource;
import com.room.management.dto.request.AssignPromotionRoomTypeRequestDto;
import com.room.management.dto.response.ApiResponse;
import com.room.management.dto.response.PromotionRoomTypeResponseDto;
import com.room.management.service.PromotionRoomTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/promotion-room-types")
@RequiredArgsConstructor
@Tag(name = "Promotion Room Type Management", description = "Assign and remove room types from promotions")
@AuthResource(value = "promotion-room-type-management", category = "PROMOTION")
public class PromotionRoomTypeController {

    private final PromotionRoomTypeService promotionRoomTypeService;

    @GetMapping("/promotion/{promotionId}")
    @AuthResource(value = "list-promotion-room-types", description = "List all room types assigned to a promotion")
    @Operation(summary = "List room types by promotion")
    public ResponseEntity<ApiResponse<List<PromotionRoomTypeResponseDto>>> getByPromotionId(
            @PathVariable Long promotionId) {
        return ResponseEntity.ok(ApiResponse.success(promotionRoomTypeService.getByPromotionId(promotionId)));
    }

    @GetMapping("/{id}")
    @AuthResource(value = "get-promotion-room-type", description = "Get promotion room type by ID")
    @Operation(summary = "Get promotion room type by ID")
    public ResponseEntity<ApiResponse<PromotionRoomTypeResponseDto>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(promotionRoomTypeService.getById(id)));
    }

    @PostMapping
    @AuthResource(value = "prt-assign", description = "Assign room types to a promotion")
    @Operation(summary = "Assign room types", description = "Assigns one or more room types to a promotion. Already assigned types are skipped.")
    public ResponseEntity<ApiResponse<List<PromotionRoomTypeResponseDto>>> assign(
            @Valid @RequestBody AssignPromotionRoomTypeRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Room types assigned successfully",
                        promotionRoomTypeService.assign(request)));
    }

    @DeleteMapping("/{id}")
    @AuthResource(value = "prt-remove", description = "Remove a room type from a promotion")
    @Operation(summary = "Remove room type", description = "Soft deletes the promotion-room type assignment by its own ID.")
    public ResponseEntity<ApiResponse<Void>> remove(@PathVariable Long id) {
        promotionRoomTypeService.remove(id);
        return ResponseEntity.ok(ApiResponse.success("Room type removed successfully", null));
    }
}
