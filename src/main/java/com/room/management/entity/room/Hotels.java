package com.room.management.entity.room;

import com.room.management.entity.auth.*;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "HOTELS")
public class Hotels extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "hotel_name", unique = true, nullable = false, length = 100)
    private String hotelName;

    @Column(name = "contact")
    private String contact;

    @Column(name = "address")
    private String address;

    @Column(name = "is_active", columnDefinition = "boolean default true")
    private Boolean isActive = true;

}
