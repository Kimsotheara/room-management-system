package com.room.management.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateUserRequestDto {

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 100, message = "Username must be between 3 and 100 characters")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @Email(message = "Invalid email format")
    private String email;

    @Size(max = 200, message = "Full name must not exceed 200 characters")
    private String fullName;

    @Size(max = 20, message = "Phone number must not exceed 20 characters")
    private String phoneNumber;
}
