package com.room.management.repository;

import com.room.management.entity.room.PromotionRoomType;
import com.room.management.entity.room.Promotions;
import com.room.management.entity.room.RoomTypes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PromotionRoomTypeRepository extends JpaRepository<PromotionRoomType, Long> {

    boolean existsByPromotionAndRoomTypeAndIsActiveTrue(Promotions promotion, RoomTypes roomType);

    Optional<PromotionRoomType> findByPromotionAndRoomTypeAndIsActiveTrue(Promotions promotion, RoomTypes roomType);

    @Query("""
            SELECT prt FROM PromotionRoomType prt
            JOIN FETCH prt.promotion
            JOIN FETCH prt.roomType
            WHERE prt.promotion.id = :promotionId
              AND prt.isActive = true
            """)
    List<PromotionRoomType> findActiveByPromotionId(@Param("promotionId") Long promotionId);

    @Query("""
            SELECT prt FROM PromotionRoomType prt
            JOIN FETCH prt.promotion
            JOIN FETCH prt.roomType
            WHERE prt.id = :id
            """)
    Optional<PromotionRoomType> findByIdWithDetails(@Param("id") Long id);
}
