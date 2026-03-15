package com.room.management.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserRequestDto {

    private String username;
    private String email;
    private String fullName;
    private String phoneNumber;
    private Boolean isActive;
   private Long roleId;

}
