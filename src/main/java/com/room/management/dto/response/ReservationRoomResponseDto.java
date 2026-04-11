package com.room.management.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReservationRoomResponseDto {

    private Long id;
    private Long roomId;
    private String roomNumber;
    private String roomTypeName;
    private Long promotionId;
    private String promotionName;
    private BigDecimal basePrice;
    private BigDecimal discountAmount;
    private BigDecimal finalPrice;
}
