package com.room.management.dto.request;

import lombok.Data;

@Data
public class UserRequestDto {

    private String username;
    private String email;
    private String fullName;
    private String phoneNumber;
    private Boolean isActive;
    private Long roleId;
}
