package com.room.management.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CreateReservationRequestDto {

    @NotNull(message = "Guest ID is required")
    private Long guestId;

    @NotNull(message = "Check-in date is required")
    private LocalDateTime checkInDate;

    @NotNull(message = "Check-out date is required")
    private LocalDateTime checkOutDate;

    @NotEmpty(message = "At least one room is required")
    @Valid
    private List<ReservationRoomRequestDto> rooms;

    private String notes;
}
