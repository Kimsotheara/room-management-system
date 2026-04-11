package com.room.management.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PromotionRoomTypeResponseDto {

    private Long id;
    private Long promotionId;
    private String promotionName;
    private Long roomTypeId;
    private String roomTypeName;
    private Boolean isActive;
    private LocalDateTime createdAt;
}
