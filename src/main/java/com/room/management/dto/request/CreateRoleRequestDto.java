package com.room.management.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateRoleRequestDto {

    @NotBlank(message = "Role name is required")
    @Size(max = 100, message = "Role name must not exceed 100 characters")
    private String roleName;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String roleDescription;
}
