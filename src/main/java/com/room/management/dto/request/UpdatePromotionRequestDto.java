package com.room.management.dto.request;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class UpdatePromotionRequestDto {

    private String name;
    private String discountType;
    private BigDecimal discountValue;
    private LocalDateTime effectiveDate;
    private LocalDateTime expireDate;
    private Boolean isActive;
}
