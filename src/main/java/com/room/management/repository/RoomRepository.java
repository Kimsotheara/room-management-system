package com.room.management.repository;

import com.room.management.entity.room.Rooms;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<Rooms, Long> {

    boolean existsByRoomNumber(String roomNumber);

    boolean existsByRoomNumberAndIdNot(String roomNumber, Long id);
}
