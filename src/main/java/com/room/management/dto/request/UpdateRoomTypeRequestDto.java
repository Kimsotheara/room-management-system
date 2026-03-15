package com.room.management.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateRoomTypeRequestDto {

    @Size(max = 100, message = "Type name must not exceed 100 characters")
    private String typeName;

    private Integer bed;

    private BigDecimal price;

    private Boolean isActive;

}
