package com.room.management.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateRoomRequestDto {

    @NotBlank(message = "Room number is required")
    @Size(max = 100, message = "Room number must not exceed 100 characters")
    private String roomNumber;

    @NotNull(message = "Room type ID is required")
    private Long roomTypeId;

    @Size(max = 50, message = "Room status must not exceed 50 characters")
    private String roomStatus;
}
