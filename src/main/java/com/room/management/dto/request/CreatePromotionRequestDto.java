package com.room.management.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CreatePromotionRequestDto {

    @NotBlank(message = " name is required")
    @Size(max = 100, message = " name must not exceed 100 characters")
    private String name;
    private String discountType;
    private BigDecimal discountValue;
    private LocalDateTime effectiveDate;
    private LocalDateTime expireDate;
}
