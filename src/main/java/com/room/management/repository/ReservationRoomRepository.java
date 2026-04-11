package com.room.management.repository;

import com.room.management.entity.room.ReservationRooms;
import com.room.management.enums.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRoomRepository extends JpaRepository<ReservationRooms, Long> {

    @Query("""
            SELECT COUNT(rr) FROM ReservationRooms rr
            WHERE rr.room.id = :roomId
              AND rr.isActive = true
              AND rr.reservation.status IN :blockingStatuses
              AND rr.reservation.checkInDate < :checkOutDate
              AND rr.reservation.checkOutDate > :checkInDate
            """)
    long countOverlappingReservations(
            @Param("roomId") Long roomId,
            @Param("checkInDate") LocalDateTime checkInDate,
            @Param("checkOutDate") LocalDateTime checkOutDate,
            @Param("blockingStatuses") List<ReservationStatus> blockingStatuses);
}
