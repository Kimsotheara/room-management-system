package com.room.management.repository;

import com.room.management.entity.room.Hotels;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HotelRepository extends JpaRepository<Hotels, Long> {

    boolean existsByHotelName(String hotelName);
}
