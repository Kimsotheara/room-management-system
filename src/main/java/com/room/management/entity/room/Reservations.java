package com.room.management.entity.room;

import com.room.management.entity.auth.BaseEntity;
import com.room.management.enums.PaymentStatus;
import com.room.management.enums.ReservationStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "RESERVATIONS")
public class Reservations extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "guest_id", nullable = false)
    private Guests guest;

    @Column(name = "check_in_date", nullable = false)
    private LocalDateTime checkInDate;

    @Column(name = "check_out_date", nullable = false)
    private LocalDateTime checkOutDate;

    @Column(name = "total_amount", precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "paid_amount", precision = 10, scale = 2)
    private BigDecimal paidAmount = BigDecimal.ZERO;

    @Column(name = "balance_amount", precision = 10, scale = 2)
    private BigDecimal balanceAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", length = 20)
    private PaymentStatus paymentStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private ReservationStatus status;

    @Column(name = "notes", length = 500)
    private String notes;

    @Column(name = "is_active", columnDefinition = "boolean default true")
    private Boolean isActive = true;

    @Column(name = "service_charge_total", precision = 10, scale = 2)
    private BigDecimal serviceChargeTotal = BigDecimal.ZERO;

    @BatchSize(size = 30)
    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ReservationRooms> reservationRooms = new ArrayList<>();

    @BatchSize(size = 30)
    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ServiceUsages> serviceUsages = new ArrayList<>();

    @BatchSize(size = 30)
    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Payments> payments = new ArrayList<>();

    public List<ReservationRooms> getActiveRooms() {
        return reservationRooms.stream()
                .filter(rr -> Boolean.TRUE.equals(rr.getIsActive()))
                .toList();
    }

    public List<ServiceUsages> getActiveServiceUsages() {
        return serviceUsages.stream()
                .filter(su -> Boolean.TRUE.equals(su.getIsActive()))
                .toList();
    }

    public List<Payments> getActivePayments() {
        return payments.stream()
                .filter(p -> Boolean.TRUE.equals(p.getIsActive()))
                .toList();
    }
}
