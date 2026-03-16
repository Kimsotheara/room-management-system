package com.room.management.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateGuestRequestDto {

    @NotBlank(message = "First name is required")
    @Size(max = 100, message = "First name must not exceed 100 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 100, message = "Last name must not exceed 100 characters")
    private String lastName;

    @Email(message = "Invalid email format")
    @Size(max = 200, message = "Email must not exceed 200 characters")
    private String email;

    @Size(max = 20, message = "Phone number must not exceed 20 characters")
    private String phoneNumber;

    @Size(max = 50, message = "Identity type must not exceed 50 characters")
    private String identityType;

    @Size(max = 100, message = "Identity number must not exceed 100 characters")
    private String identityNumber;

    @Size(max = 100, message = "Nationality must not exceed 100 characters")
    private String nationality;

    @Size(max = 500, message = "Address must not exceed 500 characters")
    private String address;

    private String profileImage;
}
