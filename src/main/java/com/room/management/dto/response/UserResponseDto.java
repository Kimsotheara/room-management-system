package com.room.management.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponseDto {

    private Long userId;
    private String username;
    private String email;
    private String fullName;
    private String phoneNumber;
    private Boolean isActive;
    private Set<RoleResponseDto> roles;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
