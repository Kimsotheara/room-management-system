package com.room.management.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RoomResponseDto {

    private Long roomId;
    private String roomNumber;
    private RoomTypeResponseDto roomType;
    private String roomStatus;
    private Boolean isActive;
    private List<RoomImageResponseDto> images;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
