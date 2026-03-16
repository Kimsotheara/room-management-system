package com.room.management.repository;

import com.room.management.entity.room.RoomImages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoomImageRepository extends JpaRepository<RoomImages, Long> {

    int countByRoomIdAndIsActiveTrue(Long roomId);

    @Query("SELECT i.imageData FROM RoomImages i WHERE i.id = :id AND i.isActive = true")
    Optional<byte[]> findImageDataById(@Param("id") Long id);
}
