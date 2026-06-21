package com.room.management.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RevenueReportResponseDto {

    private LocalDate from;
    private LocalDate to;

    private BigDecimal totalRoomCharges;
    private BigDecimal totalServiceCharges;
    private BigDecimal totalRevenue;
    private BigDecimal totalPaid;
    private BigDecimal totalBalance;

    private List<PaymentMethodSummaryDto> byPaymentMethod;

    @Data
    @Builder
    public static class PaymentMethodSummaryDto {
        private String paymentMethod;
        private long count;
        private BigDecimal amount;
    }
}
