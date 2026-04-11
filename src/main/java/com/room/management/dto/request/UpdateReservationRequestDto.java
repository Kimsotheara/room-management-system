package com.room.management.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateReservationRequestDto {

    @Size(max = 500, message = "Notes must not exceed 500 characters")
    private String notes;
}
