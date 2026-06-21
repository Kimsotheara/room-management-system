package com.room.management.repository;

import com.room.management.entity.room.Rooms;
import com.room.management.enums.RoomStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Rooms, Long> {

    boolean existsByRoomNumber(String roomNumber);

    boolean existsByRoomNumberAndIdNot(String roomNumber, Long id);

    @Modifying
    @Query("UPDATE Rooms r SET r.roomStatus = :status WHERE r.id IN :ids")
    void updateStatusByIds(@Param("ids") List<Long> ids, @Param("status") RoomStatus status);

    @Query("SELECT r.roomStatus, COUNT(r) FROM Rooms r WHERE r.isActive = true GROUP BY r.roomStatus")
    List<Object[]> countActiveByStatus();
}
