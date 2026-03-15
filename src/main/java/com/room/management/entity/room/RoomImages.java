package com.room.management.entity.room;

import com.room.management.entity.auth.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "ROOM_IMAGES")
public class RoomImages extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "room_id", nullable = false)
    private Rooms room;

    @Column(name = "image_data", nullable = false, columnDefinition = "TEXT")
    private String imageData;

    @Column(name = "display_order")
    private Integer displayOrder = 0;

    @Column(name = "is_active", columnDefinition = "boolean default true")
    private Boolean isActive = true;
}
