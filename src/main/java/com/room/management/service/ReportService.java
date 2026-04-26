package com.room.management.service;

import com.room.management.dto.response.DashboardResponseDto;
import com.room.management.dto.response.ReservationReportResponseDto;
import com.room.management.dto.response.RevenueReportResponseDto;

import java.time.LocalDate;

public interface ReportService {

    DashboardResponseDto getDashboard();

    RevenueReportResponseDto getRevenueReport(LocalDate from, LocalDate to);

    ReservationReportResponseDto getReservationReport(LocalDate from, LocalDate to);
}
