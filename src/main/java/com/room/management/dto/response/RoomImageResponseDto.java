package com.room.management.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RoomImageResponseDto {

    private Long imageId;
    private String imageData;
    private Integer displayOrder;
}
