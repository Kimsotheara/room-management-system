package com.room.management.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class AssignPromotionRoomTypeRequestDto {

    @NotNull(message = "Promotion ID is required")
    private Long promotionId;

    @NotEmpty(message = "At least one room type ID is required")
    private List<Long> roomTypeIds;
}
