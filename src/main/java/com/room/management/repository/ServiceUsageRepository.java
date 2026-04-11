package com.room.management.repository;

import com.room.management.entity.room.ServiceUsages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceUsageRepository extends JpaRepository<ServiceUsages, Long> {

    @Query("""
            SELECT su FROM ServiceUsages su
            JOIN FETCH su.service
            WHERE su.reservation.id = :reservationId
              AND su.isActive = true
            """)
    List<ServiceUsages> findActiveByReservationId(@Param("reservationId") Long reservationId);

    @Query("""
            SELECT su FROM ServiceUsages su
            JOIN FETCH su.service
            JOIN FETCH su.reservation
            WHERE su.id = :id
            """)
    Optional<ServiceUsages> findByIdWithDetails(@Param("id") Long id);
}
