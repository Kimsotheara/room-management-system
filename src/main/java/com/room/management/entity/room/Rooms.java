package com.room.management.entity.room;

import com.room.management.entity.auth.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "ROOMS")
public class Rooms extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "room_number", unique = true, nullable = false, length = 100)
    private String roomNumber;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "room_type_id", nullable = false)
    private RoomTypes roomTypes;

    @Column(name = "room_status", length = 50)
    private String roomStatus;

    @Column(name = "is_active", columnDefinition = "boolean default true")
    private Boolean isActive = true;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RoomImages> roomImages = new ArrayList<>();

    public List<RoomImages> getActiveImages() {
        return roomImages.stream()
                .filter(img -> Boolean.TRUE.equals(img.getIsActive()))
                .sorted(Comparator.comparingInt(RoomImages::getDisplayOrder))
                .toList();
    }
}
