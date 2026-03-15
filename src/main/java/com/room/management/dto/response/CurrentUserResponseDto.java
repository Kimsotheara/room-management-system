package com.room.management.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CurrentUserResponseDto {

    private Long userId;
    private String username;
    private String email;
    private String fullName;
    private Boolean isActive;
    private Set<String> roles;
    private List<String> permissionCodes;
}
