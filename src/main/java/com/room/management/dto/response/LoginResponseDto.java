package com.room.management.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginResponseDto {

    private String accessToken;
    private String refreshToken;
    private Long expiresIn;
    private String tokenType;
    private Long userId;
    private String username;
    private String email;
    private String fullName;
    private Boolean isActive;
    private List<String> permissionCodes;
}
