package com.room.management.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AssignRoleRequestDto {

    @NotNull(message = "Role ID is required")
    private Long roleId;

    private LocalDateTime expiresAt;
}
