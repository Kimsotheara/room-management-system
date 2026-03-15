package com.room.management.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateUserRequestDto {

    @Email(message = "Invalid email format")
    private String email;

    @Size(max = 200, message = "Full name must not exceed 200 characters")
    private String fullName;

    @Size(max = 20, message = "Phone number must not exceed 20 characters")
    private String phoneNumber;

    private Boolean isActive;
}
