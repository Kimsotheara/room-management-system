package com.room.management.dto.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateServiceRequestDto {

    private String name;
    private BigDecimal price;
    private Boolean isActive;

}
