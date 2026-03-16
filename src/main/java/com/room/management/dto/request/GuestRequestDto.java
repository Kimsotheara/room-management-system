package com.room.management.dto.request;

import lombok.Data;

@Data
public class GuestRequestDto {

    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String nationality;
    private String identityType;
    private Boolean isActive;
}
