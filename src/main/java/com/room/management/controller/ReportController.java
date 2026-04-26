package com.room.management.controller;

import com.room.management.annotation.AuthResource;
import com.room.management.dto.response.ApiResponse;
import com.room.management.dto.response.DashboardResponseDto;
import com.room.management.dto.response.ReservationReportResponseDto;
import com.room.management.dto.response.RevenueReportResponseDto;
import com.room.management.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Tag(name = "Reports", description = "Dashboard and operational reports")
@AuthResource(value = "report-management", category = "REPORT")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/dashboard")
    @AuthResource(value = "report-dashboard", description = "Real-time overview of rooms, activity, and revenue")
    @Operation(summary = "Dashboard", description = "Returns room status breakdown, today's check-ins/check-outs, active reservations, pending payments, and current month revenue.")
    public ResponseEntity<ApiResponse<DashboardResponseDto>> dashboard() {
        return ResponseEntity.ok(ApiResponse.success(reportService.getDashboard()));
    }

    @GetMapping("/revenue")
    @AuthResource(value = "report-revenue", description = "Revenue report for a date range")
    @Operation(summary = "Revenue report", description = "Returns total revenue, room vs service charge breakdown, and payment method summary for the given check-in date range.")
    public ResponseEntity<ApiResponse<RevenueReportResponseDto>> revenue(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(ApiResponse.success(reportService.getRevenueReport(from, to)));
    }

    @GetMapping("/reservations")
    @AuthResource(value = "report-reservations", description = "Reservation counts by status for a date range")
    @Operation(summary = "Reservation report", description = "Returns reservation counts grouped by status (CONFIRMED, CHECKED_IN, CHECKED_OUT, CANCELLED) for the given check-in date range.")
    public ResponseEntity<ApiResponse<ReservationReportResponseDto>> reservations(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(ApiResponse.success(reportService.getReservationReport(from, to)));
    }
}
