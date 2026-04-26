package com.room.management.repository;

import com.room.management.entity.room.Payments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payments, Long> {

    @Query("""
            SELECT p FROM Payments p
            JOIN FETCH p.reservation
            WHERE p.reservation.id = :reservationId
              AND p.isActive = true
            ORDER BY p.paymentDate ASC
            """)
    List<Payments> findActiveByReservationId(@Param("reservationId") Long reservationId);

    @Query("""
            SELECT p FROM Payments p
            JOIN FETCH p.reservation
            WHERE p.id = :id
            """)
    Optional<Payments> findByIdWithDetails(@Param("id") Long id);

    @Query("""
            SELECT p.paymentMethod, SUM(p.amount), COUNT(p)
            FROM Payments p
            WHERE p.isActive = true
              AND p.paymentDate >= :from AND p.paymentDate < :to
            GROUP BY p.paymentMethod
            """)
    List<Object[]> sumByPaymentMethodInDateRange(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);
}
