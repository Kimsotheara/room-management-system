package com.room.management.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateRoomRequestDto {

    @Size(max = 100, message = "Room number must not exceed 100 characters")
    private String roomNumber;

    private Long roomTypeId;

    @Size(max = 50, message = "Room status must not exceed 50 characters")
    private String roomStatus;

    private Boolean isActive;
}
