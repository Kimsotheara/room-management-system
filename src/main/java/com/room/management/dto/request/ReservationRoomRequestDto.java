package com.room.management.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReservationRoomRequestDto {

    @NotNull(message = "Room ID is required")
    private Long roomId;

    private Long promotionId;
}
