package com.room.management.service.impl;

import com.room.management.dto.request.PaymentRequestDto;
import com.room.management.dto.response.PaymentResponseDto;
import com.room.management.entity.room.Payments;
import com.room.management.entity.room.Reservations;
import com.room.management.enums.PaymentStatus;
import com.room.management.enums.ReservationStatus;
import com.room.management.exception.ResourceNotFoundException;
import com.room.management.mapper.PaymentMapper;
import com.room.management.repository.PaymentRepository;
import com.room.management.repository.ReservationRepository;
import com.room.management.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;
    private final PaymentMapper paymentMapper;

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponseDto> getByReservationId(Long reservationId) {
        if (!reservationRepository.existsById(reservationId)) {
            throw new ResourceNotFoundException("Reservation", reservationId);
        }
        return paymentMapper.toDtoList(paymentRepository.findActiveByReservationId(reservationId));
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponseDto getById(Long id) {
        return paymentMapper.toDto(findByIdWithDetails(id));
    }

    @Override
    @Transactional
    public PaymentResponseDto add(PaymentRequestDto request) {
        Reservations reservation = reservationRepository.findByIdWithDetails(request.getReservationId())
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", request.getReservationId()));

        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new IllegalArgumentException("Cannot add payment to a cancelled reservation");
        }
        if (reservation.getPaymentStatus() == PaymentStatus.PAID) {
            throw new IllegalArgumentException("Reservation is already fully paid");
        }

        BigDecimal newPaidAmount = reservation.getPaidAmount().add(request.getAmount());
        if (newPaidAmount.compareTo(reservation.getTotalAmount()) > 0) {
            throw new IllegalArgumentException(
                    "Payment exceeds remaining balance of " + reservation.getBalanceAmount());
        }

        Payments payment = new Payments();
        payment.setReservation(reservation);
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setPaymentType(request.getPaymentType());
        payment.setAmount(request.getAmount());
        payment.setPaymentDate(LocalDateTime.now());
        payment.setIsActive(true);
        Payments saved = paymentRepository.save(payment);

        reservation.setPaidAmount(newPaidAmount);
        reservation.setBalanceAmount(reservation.getTotalAmount().subtract(newPaidAmount));
        reservation.setPaymentStatus(resolvePaymentStatus(newPaidAmount, reservation.getTotalAmount()));
        reservationRepository.save(reservation);

        log.info("Payment added: id={}, reservationId={}, method={}, type={}, amount={}, paymentStatus={}",
                saved.getId(), reservation.getId(), request.getPaymentMethod(),
                request.getPaymentType(), request.getAmount(), reservation.getPaymentStatus());

        return paymentMapper.toDto(saved);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Payments payment = findByIdWithDetails(id);

        if (!Boolean.TRUE.equals(payment.getIsActive())) {
            throw new IllegalArgumentException("Payment is already deleted");
        }

        Reservations reservation = payment.getReservation();
        BigDecimal newPaidAmount = reservation.getPaidAmount().subtract(payment.getAmount());
        reservation.setPaidAmount(newPaidAmount);
        reservation.setBalanceAmount(reservation.getTotalAmount().subtract(newPaidAmount));
        reservation.setPaymentStatus(resolvePaymentStatus(newPaidAmount, reservation.getTotalAmount()));
        reservationRepository.save(reservation);

        payment.setIsActive(false);
        paymentRepository.save(payment);

        log.info("Payment deleted: id={}, reservationId={}, reversed={}",
                id, reservation.getId(), payment.getAmount());
    }

    // ── Helpers ────────────────────────────────────────────────────────────────

    private Payments findByIdWithDetails(Long id) {
        return paymentRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", id));
    }

    private PaymentStatus resolvePaymentStatus(BigDecimal paid, BigDecimal total) {
        if (paid.compareTo(BigDecimal.ZERO) == 0) return PaymentStatus.UNPAID;
        if (paid.compareTo(total) >= 0) return PaymentStatus.PAID;
        return PaymentStatus.PARTIAL;
    }
}
