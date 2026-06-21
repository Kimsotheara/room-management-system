package com.room.management.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DashboardResponseDto {

    // Room availability
    private long totalRooms;
    private long availableRooms;
    private long reservedRooms;
    private long occupiedRooms;
    private long maintenanceRooms;
    private long cleaningRooms;
    private double occupancyRate; // percentage, e.g. 75.5

    // Today's activity
    private long todayCheckIns;
    private long todayCheckOuts;

    // Active reservations
    private long confirmedReservations;
    private long checkedInReservations;

    // Payments
    private long pendingPaymentReservations; // UNPAID + PARTIAL

    // Current month revenue
    private BigDecimal monthlyRevenue;
    private BigDecimal monthlyPaid;
    private BigDecimal monthlyBalance;
}
