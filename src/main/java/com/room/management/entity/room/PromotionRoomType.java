package com.room.management.entity.room;

import com.room.management.entity.auth.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "PROMOTION_ROOM_TYPES")
public class PromotionRoomType extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "promotion_id", nullable = false)
    private Promotions promotion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "room_type_id", nullable = false)
    private RoomTypes roomType;

    @Column(name = "is_active", columnDefinition = "boolean default true")
    private Boolean isActive = true;
}
