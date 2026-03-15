package com.room.management.entity.room;

import com.room.management.entity.auth.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "ROOM_TYPES")
public class RoomTypes extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "type_name", unique = true, nullable = false, length = 100)
    private String typeName;

    @Column(name = "bed")
    private Integer bed;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "is_active", columnDefinition = "boolean default true")
    private Boolean isActive = true;

}
