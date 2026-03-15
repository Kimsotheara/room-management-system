package com.room.management.repository;

import com.room.management.entity.room.RoomTypes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomTypeRepository extends JpaRepository<RoomTypes, Long> {

    boolean existsByTypeName(String typeName);
}
