package com.room.management.service.impl;

import com.room.management.dto.request.AddServiceUsageRequestDto;
import com.room.management.dto.response.ServiceUsageResponseDto;
import com.room.management.entity.room.Reservations;
import com.room.management.entity.room.ServiceUsages;
import com.room.management.entity.room.Services;
import com.room.management.enums.PaymentStatus;
import com.room.management.enums.ReservationStatus;
import com.room.management.exception.ResourceNotFoundException;
import com.room.management.mapper.ServiceUsageMapper;
import com.room.management.repository.ReservationRepository;
import com.room.management.repository.ServiceUsageRepository;
import com.room.management.repository.ServicesRepository;
import com.room.management.service.ServiceUsageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServiceUsageServiceImpl implements ServiceUsageService {

    private final ServiceUsageRepository serviceUsageRepository;
    private final ServicesRepository servicesRepository;
    private final ReservationRepository reservationRepository;
    private final ServiceUsageMapper serviceUsageMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ServiceUsageResponseDto> getByReservationId(Long reservationId) {
        if (!reservationRepository.existsById(reservationId)) {
            throw new ResourceNotFoundException("Reservation", reservationId);
        }
        return serviceUsageMapper.toDtoList(
                serviceUsageRepository.findActiveByReservationId(reservationId));
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceUsageResponseDto getById(Long id) {
        return serviceUsageMapper.toDto(findByIdWithDetails(id));
    }

    @Override
    @Transactional
    public ServiceUsageResponseDto add(AddServiceUsageRequestDto request) {
        Reservations reservation = reservationRepository.findByIdWithDetails(request.getReservationId())
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", request.getReservationId()));

        if (reservation.getStatus() != ReservationStatus.CHECKED_IN) {
            throw new IllegalArgumentException(
                    "Can only add services to a CHECKED_IN reservation. Current status: " + reservation.getStatus());
        }

        Services service = servicesRepository.findById(request.getServiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Service", request.getServiceId()));

        if (!Boolean.TRUE.equals(service.getIsActive())) {
            throw new IllegalArgumentException("Service '" + service.getName() + "' is not available");
        }

        BigDecimal unitPrice = service.getPrice();
        BigDecimal totalPrice = unitPrice.multiply(BigDecimal.valueOf(request.getQuantity()));

        ServiceUsages usage = new ServiceUsages();
        usage.setReservation(reservation);
        usage.setService(service);
        usage.setQuantity(request.getQuantity());
        usage.setUnitPrice(unitPrice);
        usage.setTotalPrice(totalPrice);
        usage.setIsActive(true);
        ServiceUsages saved = serviceUsageRepository.save(usage);

        // Update reservation totals
        BigDecimal newTotalAmount = reservation.getTotalAmount().add(totalPrice);
        reservation.setServiceChargeTotal(reservation.getServiceChargeTotal().add(totalPrice));
        reservation.setTotalAmount(newTotalAmount);
        reservation.setBalanceAmount(newTotalAmount.subtract(reservation.getPaidAmount()));
        reservation.setPaymentStatus(resolvePaymentStatus(reservation.getPaidAmount(), newTotalAmount));
        reservationRepository.save(reservation);

        log.info("Service usage added: reservationId={}, service='{}', qty={}, total={}",
                reservation.getId(), service.getName(), request.getQuantity(), totalPrice);

        return serviceUsageMapper.toDto(saved);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        ServiceUsages usage = findByIdWithDetails(id);

        if (!Boolean.TRUE.equals(usage.getIsActive())) {
            throw new IllegalArgumentException("Service usage is already deleted");
        }

        // Reverse the reservation totals
        Reservations reservation = usage.getReservation();
        BigDecimal newTotalAmount = reservation.getTotalAmount().subtract(usage.getTotalPrice());
        reservation.setServiceChargeTotal(reservation.getServiceChargeTotal().subtract(usage.getTotalPrice()));
        reservation.setTotalAmount(newTotalAmount);
        reservation.setBalanceAmount(newTotalAmount.subtract(reservation.getPaidAmount()));
        reservation.setPaymentStatus(resolvePaymentStatus(reservation.getPaidAmount(), newTotalAmount));
        reservationRepository.save(reservation);

        usage.setIsActive(false);
        serviceUsageRepository.save(usage);

        log.info("Service usage deleted: id={}, reservationId={}, reversed={}",
                id, reservation.getId(), usage.getTotalPrice());
    }

    // ── Helpers ────────────────────────────────────────────────────────────────

    private ServiceUsages findByIdWithDetails(Long id) {
        return serviceUsageRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service Usage", id));
    }

    private PaymentStatus resolvePaymentStatus(BigDecimal paid, BigDecimal total) {
        if (paid.compareTo(BigDecimal.ZERO) == 0) return PaymentStatus.UNPAID;
        if (paid.compareTo(total) >= 0) return PaymentStatus.PAID;
        return PaymentStatus.PARTIAL;
    }
}
