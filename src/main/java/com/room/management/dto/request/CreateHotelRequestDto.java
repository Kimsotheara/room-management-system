package com.room.management.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateHotelRequestDto {

    @NotBlank(message = "Hotel name is required")
    @Size(max = 100, message = "Hotel name must not exceed 100 characters")
    private String hotelName;

    private String contact;

    private String address;
}
