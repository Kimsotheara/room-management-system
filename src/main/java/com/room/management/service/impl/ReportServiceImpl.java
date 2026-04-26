package com.room.management.service.impl;

import com.room.management.dto.response.DashboardResponseDto;
import com.room.management.dto.response.ReservationReportResponseDto;
import com.room.management.dto.response.RevenueReportResponseDto;
import com.room.management.enums.PaymentStatus;
import com.room.management.enums.ReservationStatus;
import com.room.management.enums.RoomStatus;
import com.room.management.repository.PaymentRepository;
import com.room.management.repository.ReservationRepository;
import com.room.management.repository.RoomRepository;
import com.room.management.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;
    private final PaymentRepository paymentRepository;

    @Override
    @Transactional(readOnly = true)
    public DashboardResponseDto getDashboard() {
        // Room status breakdown
        Map<RoomStatus, Long> roomCounts = roomRepository.countActiveByStatus().stream()
                .collect(Collectors.toMap(r -> (RoomStatus) r[0], r -> (Long) r[1]));

        long total = roomCounts.values().stream().mapToLong(Long::longValue).sum();
        long available = roomCounts.getOrDefault(RoomStatus.AVAILABLE, 0L);
        long reserved = roomCounts.getOrDefault(RoomStatus.RESERVED, 0L);
        long occupied = roomCounts.getOrDefault(RoomStatus.OCCUPIED, 0L);
        long maintenance = roomCounts.getOrDefault(RoomStatus.MAINTENANCE, 0L);
        long cleaning = roomCounts.getOrDefault(RoomStatus.CLEANING, 0L);
        double occupancyRate = total > 0 ? Math.round((double) occupied / total * 10000.0) / 100.0 : 0;

        // Today's activity
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        long todayCheckIns = reservationRepository.countTodayCheckIns(startOfDay, endOfDay);
        long todayCheckOuts = reservationRepository.countTodayCheckOuts(startOfDay, endOfDay);

        // Active reservation breakdown
        Map<ReservationStatus, Long> resCounts = reservationRepository.countActiveByStatus().stream()
                .collect(Collectors.toMap(r -> (ReservationStatus) r[0], r -> (Long) r[1]));
        long confirmed = resCounts.getOrDefault(ReservationStatus.CONFIRMED, 0L);
        long checkedIn = resCounts.getOrDefault(ReservationStatus.CHECKED_IN, 0L);

        // Pending payments
        long pendingPayments = reservationRepository.countByPaymentStatuses(
                List.of(PaymentStatus.UNPAID, PaymentStatus.PARTIAL));

        // Current month revenue
        LocalDateTime monthStart = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        Object[] monthly = reservationRepository.revenueByDateRange(monthStart, endOfDay);
        BigDecimal monthlyRevenue = asBigDecimal(monthly[0]);
        BigDecimal monthlyPaid = asBigDecimal(monthly[1]);
        BigDecimal monthlyBalance = asBigDecimal(monthly[2]);

        return DashboardResponseDto.builder()
                .totalRooms(total)
                .availableRooms(available)
                .reservedRooms(reserved)
                .occupiedRooms(occupied)
                .maintenanceRooms(maintenance)
                .cleaningRooms(cleaning)
                .occupancyRate(occupancyRate)
                .todayCheckIns(todayCheckIns)
                .todayCheckOuts(todayCheckOuts)
                .confirmedReservations(confirmed)
                .checkedInReservations(checkedIn)
                .pendingPaymentReservations(pendingPayments)
                .monthlyRevenue(monthlyRevenue)
                .monthlyPaid(monthlyPaid)
                .monthlyBalance(monthlyBalance)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public RevenueReportResponseDto getRevenueReport(LocalDate from, LocalDate to) {
        if (to.isBefore(from)) {
            throw new IllegalArgumentException("End date must not be before start date");
        }
        LocalDateTime fromDt = from.atStartOfDay();
        LocalDateTime toDt = to.plusDays(1).atStartOfDay();

        Object[] row = reservationRepository.revenueByDateRange(fromDt, toDt);
        BigDecimal totalRevenue = asBigDecimal(row[0]);
        BigDecimal totalPaid = asBigDecimal(row[1]);
        BigDecimal totalBalance = asBigDecimal(row[2]);
        BigDecimal serviceCharges = asBigDecimal(row[3]);
        BigDecimal roomCharges = totalRevenue.subtract(serviceCharges);

        List<RevenueReportResponseDto.PaymentMethodSummaryDto> byMethod =
                paymentRepository.sumByPaymentMethodInDateRange(fromDt, toDt).stream()
                        .map(r -> RevenueReportResponseDto.PaymentMethodSummaryDto.builder()
                                .paymentMethod((String) r[0])
                                .amount((BigDecimal) r[1])
                                .count((Long) r[2])
                                .build())
                        .toList();

        return RevenueReportResponseDto.builder()
                .from(from)
                .to(to)
                .totalRoomCharges(roomCharges)
                .totalServiceCharges(serviceCharges)
                .totalRevenue(totalRevenue)
                .totalPaid(totalPaid)
                .totalBalance(totalBalance)
                .byPaymentMethod(byMethod)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ReservationReportResponseDto getReservationReport(LocalDate from, LocalDate to) {
        if (to.isBefore(from)) {
            throw new IllegalArgumentException("End date must not be before start date");
        }
        LocalDateTime fromDt = from.atStartOfDay();
        LocalDateTime toDt = to.plusDays(1).atStartOfDay();

        Map<ReservationStatus, Long> counts = reservationRepository
                .countByStatusInDateRange(fromDt, toDt).stream()
                .collect(Collectors.toMap(r -> (ReservationStatus) r[0], r -> (Long) r[1]));

        long confirmed = counts.getOrDefault(ReservationStatus.CONFIRMED, 0L);
        long checkedIn = counts.getOrDefault(ReservationStatus.CHECKED_IN, 0L);
        long checkedOut = counts.getOrDefault(ReservationStatus.CHECKED_OUT, 0L);
        long cancelled = counts.getOrDefault(ReservationStatus.CANCELLED, 0L);

        return ReservationReportResponseDto.builder()
                .from(from)
                .to(to)
                .total(confirmed + checkedIn + checkedOut + cancelled)
                .confirmed(confirmed)
                .checkedIn(checkedIn)
                .checkedOut(checkedOut)
                .cancelled(cancelled)
                .build();
    }

    // ── Helpers ────────────────────────────────────────────────────────────────

    private BigDecimal asBigDecimal(Object value) {
        return value instanceof BigDecimal bd ? bd : BigDecimal.ZERO;
    }
}
