package com.room.management.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GuestResponseDto {

    private Long guestId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String identityType;
    private String identityNumber;
    private String nationality;
    private String address;
    private String profileImage;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
