package com.room.management.repository;

import com.room.management.entity.room.Reservations;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
}
