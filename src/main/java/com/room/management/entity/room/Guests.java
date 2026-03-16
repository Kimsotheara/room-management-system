package com.room.management.entity.room;

import com.room.management.entity.auth.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "GUESTS")
public class Guests extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "email", unique = true, length = 200)
    private String email;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "identity_type", length = 50)
    private String identityType;

    @Column(name = "identity_number", unique = true, length = 100)
    private String identityNumber;

    @Column(name = "nationality", length = 100)
    private String nationality;

    @Column(name = "address", length = 500)
    private String address;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "profile_image")
    private byte[] profileImage;

    @Column(name = "is_active", columnDefinition = "boolean default true")
    private Boolean isActive = true;
}
