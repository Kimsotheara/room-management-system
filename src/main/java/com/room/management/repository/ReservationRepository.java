package com.room.management.repository;

import com.room.management.entity.room.Reservations;
import com.room.management.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservations, Long> {

    @Query("""
            SELECT r FROM Reservations r
            JOIN FETCH r.guest
            WHERE r.isActive = true
            ORDER BY r.createdAt DESC
            """)
    List<Reservations> findAllActive();

    @Query("""
            SELECT DISTINCT r FROM Reservations r
            JOIN FETCH r.guest
            LEFT JOIN FETCH r.reservationRooms rr
            LEFT JOIN FETCH rr.room room
            LEFT JOIN FETCH room.roomTypes
            LEFT JOIN FETCH rr.promotion
            WHERE r.id = :id
            """)
    Optional<Reservations> findByIdWithDetails(@Param("id") Long id);

    // ── Report queries ──────────────────────────────────────────────────────────

    @Query("SELECT r.status, COUNT(r) FROM Reservations r WHERE r.isActive = true GROUP BY r.status")
    List<Object[]> countActiveByStatus();

    @Query("""
            SELECT COUNT(r) FROM Reservations r
            WHERE r.isActive = true
              AND r.checkInDate >= :start AND r.checkInDate < :end
              AND r.status = com.room.management.enums.ReservationStatus.CHECKED_IN
            """)
    long countTodayCheckIns(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("""
            SELECT COUNT(r) FROM Reservations r
            WHERE r.isActive = true
              AND r.checkOutDate >= :start AND r.checkOutDate < :end
              AND r.status = com.room.management.enums.ReservationStatus.CHECKED_OUT
            """)
    long countTodayCheckOuts(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COUNT(r) FROM Reservations r WHERE r.isActive = true AND r.paymentStatus IN :statuses")
    long countByPaymentStatuses(@Param("statuses") List<PaymentStatus> statuses);

    @Query("""
            SELECT SUM(r.totalAmount), SUM(r.paidAmount), SUM(r.balanceAmount), SUM(r.serviceChargeTotal)
            FROM Reservations r
            WHERE r.isActive = true
              AND r.status != com.room.management.enums.ReservationStatus.CANCELLED
              AND r.checkInDate >= :from AND r.checkInDate < :to
            """)
    List<Object[]> revenueByDateRange(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("""
            SELECT r.status, COUNT(r)
            FROM Reservations r
            WHERE r.isActive = true
              AND r.checkInDate >= :from AND r.checkInDate < :to
            GROUP BY r.status
            """)
    List<Object[]> countByStatusInDateRange(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);
}
