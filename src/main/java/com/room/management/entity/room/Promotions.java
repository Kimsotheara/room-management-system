package com.room.management.entity.room;

import com.room.management.entity.auth.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "PROMOTIONS")
public class Promotions extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", unique = true, nullable = false, length = 100)
    private String name;
    private String discountType;
    private BigDecimal discountValue;
    private LocalDateTime effectiveDate;
    private LocalDateTime expireDate;

    @Column(name = "is_active", columnDefinition = "boolean default true")
    private Boolean isActive = true;

}
