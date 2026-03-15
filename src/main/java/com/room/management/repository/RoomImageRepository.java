package com.room.management.repository;

import com.room.management.entity.room.RoomImages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomImageRepository extends JpaRepository<RoomImages, Long> {

    List<RoomImages> findByRoomIdAndIsActiveTrueOrderByDisplayOrderAsc(Long roomId);

    int countByRoomIdAndIsActiveTrue(Long roomId);
}
