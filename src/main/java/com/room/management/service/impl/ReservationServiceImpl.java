package com.room.management.service.impl;

import com.room.management.dto.request.CreateReservationRequestDto;
import com.room.management.dto.request.PaymentRequestDto;
import com.room.management.dto.request.ReservationRoomRequestDto;
import com.room.management.dto.request.UpdateReservationRequestDto;
import com.room.management.dto.response.InvoiceResponseDto;
import com.room.management.dto.response.ReservationResponseDto;
import com.room.management.entity.room.Promotions;
import com.room.management.entity.room.ReservationRooms;
import com.room.management.entity.room.Reservations;
import com.room.management.entity.room.Rooms;
import com.room.management.enums.PaymentStatus;
import com.room.management.enums.ReservationStatus;
import com.room.management.enums.RoomStatus;
import com.room.management.exception.ResourceNotFoundException;
import com.room.management.mapper.ReservationMapper;
import com.room.management.repository.GuestRepository;
import com.room.management.repository.PromotionRepository;
import com.room.management.repository.PromotionRoomTypeRepository;
import com.room.management.repository.ReservationRepository;
import com.room.management.repository.ReservationRoomRepository;
import com.room.management.repository.RoomRepository;
import com.room.management.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private static final List<ReservationStatus> BLOCKING_STATUSES = List.of(ReservationStatus.CONFIRMED, ReservationStatus.CHECKED_IN);

    private final ReservationRepository reservationRepository;
    private final ReservationRoomRepository reservationRoomRepository;
    private final GuestRepository guestRepository;
    private final RoomRepository roomRepository;
    private final PromotionRepository promotionRepository;
    private final PromotionRoomTypeRepository promotionRoomTypeRepository;
    private final ReservationMapper reservationMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ReservationResponseDto> getAll() {
        return reservationRepository.findAllActive().stream()
                .map(reservationMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ReservationResponseDto getById(Long id) {
        return reservationMapper.toDto(findById(id));
    }

    @Override
    @Transactional
    public ReservationResponseDto create(CreateReservationRequestDto request) {
        validateDates(request.getCheckInDate(), request.getCheckOutDate());

        long nights = ChronoUnit.DAYS.between(
                request.getCheckInDate().toLocalDate(),
                request.getCheckOutDate().toLocalDate());

        var guest = guestRepository.findById(request.getGuestId())
                .orElseThrow(() -> new ResourceNotFoundException("Guest", request.getGuestId()));

        Reservations reservation = new Reservations();
        reservation.setGuest(guest);
        reservation.setCheckInDate(request.getCheckInDate());
        reservation.setCheckOutDate(request.getCheckOutDate());
        reservation.setNotes(request.getNotes());
        reservation.setStatus(ReservationStatus.CONFIRMED);
        reservation.setPaidAmount(BigDecimal.ZERO);
        reservation.setIsActive(true);

        Reservations saved = reservationRepository.save(reservation);

        List<ReservationRooms> reservationRooms = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (ReservationRoomRequestDto roomRequest : request.getRooms()) {
            Rooms room = roomRepository.findById(roomRequest.getRoomId())
                    .orElseThrow(() -> new ResourceNotFoundException("Room", roomRequest.getRoomId()));

            if (!Boolean.TRUE.equals(room.getIsActive())) {
                throw new IllegalArgumentException("Room " + room.getRoomNumber() + " is not active");
            }

            long overlaps = reservationRoomRepository.countOverlappingReservations(
                    room.getId(), request.getCheckInDate(), request.getCheckOutDate(), BLOCKING_STATUSES);
            if (overlaps > 0) {
                throw new IllegalArgumentException(
                        "Room " + room.getRoomNumber() + " is already booked for the selected dates");
            }

            BigDecimal basePrice = room.getRoomTypes().getPrice().multiply(BigDecimal.valueOf(nights));
            BigDecimal discountAmount = BigDecimal.ZERO;
            Promotions promotion = null;

            // Promotion is optional — no promotionId means full price, not an error
            if (roomRequest.getPromotionId() != null) {
                promotion = promotionRepository.findById(roomRequest.getPromotionId())
                        .orElseThrow(() -> new ResourceNotFoundException("Promotion", roomRequest.getPromotionId()));
                validatePromotion(promotion, room, request.getCheckInDate());
                discountAmount = calculateDiscount(basePrice, promotion);
            }

            BigDecimal finalPrice = basePrice.subtract(discountAmount).max(BigDecimal.ZERO);
            totalAmount = totalAmount.add(finalPrice);

            ReservationRooms rr = new ReservationRooms();
            rr.setReservation(saved);
            rr.setRoom(room);
            rr.setPromotion(promotion);
            rr.setBasePrice(basePrice);
            rr.setDiscountAmount(discountAmount);
            rr.setFinalPrice(finalPrice);
            rr.setIsActive(true);
            reservationRooms.add(rr);
        }

        reservationRoomRepository.saveAll(reservationRooms);

        // Bulk-update all booked rooms to RESERVED in a single query
        List<Long> roomIds = reservationRooms.stream().map(rr -> rr.getRoom().getId()).toList();
        reservationRooms.forEach(rr -> rr.getRoom().setRoomStatus(RoomStatus.RESERVED));
        roomRepository.updateStatusByIds(roomIds, RoomStatus.RESERVED);

        saved.setTotalAmount(totalAmount);
        saved.setBalanceAmount(totalAmount);
        saved.setPaymentStatus(PaymentStatus.UNPAID);
        reservationRepository.save(saved);

        log.info("Reservation created: id={}, guest={}, rooms={}, total={}",
                saved.getId(), guest.getFirstName() + " " + guest.getLastName(),
                reservationRooms.size(), totalAmount);

        // Fetch with JOIN FETCH to populate rooms for the response
        return reservationMapper.toDto(findById(saved.getId()));
    }

    @Override
    @Transactional
    public ReservationResponseDto update(Long id, UpdateReservationRequestDto request) {
        Reservations reservation = findById(id);

        if (StringUtils.hasText(request.getNotes())) {
            reservation.setNotes(request.getNotes());
        }

        reservationRepository.save(reservation);
        log.info("Reservation updated: id={}", id);
        return reservationMapper.toDto(reservation);
    }

    @Override
    @Transactional
    public ReservationResponseDto checkIn(Long id) {
        Reservations reservation = findById(id);

        if (reservation.getStatus() != ReservationStatus.CONFIRMED) {
            throw new IllegalArgumentException(
                    "Cannot check in. Reservation status is: " + reservation.getStatus() +
                    ". Only CONFIRMED reservations can be checked in.");
        }

        reservation.setStatus(ReservationStatus.CHECKED_IN);
        reservationRepository.save(reservation);

        List<ReservationRooms> activeRooms = reservation.getActiveRooms();
        activeRooms.forEach(rr -> rr.getRoom().setRoomStatus(RoomStatus.OCCUPIED));
        roomRepository.updateStatusByIds(
                activeRooms.stream().map(rr -> rr.getRoom().getId()).toList(),
                RoomStatus.OCCUPIED);

        log.info("Reservation checked in: id={}", id);
        return reservationMapper.toDto(reservation);
    }

    @Override
    @Transactional
    public ReservationResponseDto checkOut(Long id) {
        Reservations reservation = findById(id);

        if (reservation.getStatus() != ReservationStatus.CHECKED_IN) {
            throw new IllegalArgumentException(
                    "Cannot check out. Reservation status is: " + reservation.getStatus() +
                    ". Only CHECKED_IN reservations can be checked out.");
        }

        reservation.setStatus(ReservationStatus.CHECKED_OUT);
        reservationRepository.save(reservation);

        List<ReservationRooms> activeRooms = reservation.getActiveRooms();
        activeRooms.forEach(rr -> rr.getRoom().setRoomStatus(RoomStatus.AVAILABLE));
        roomRepository.updateStatusByIds(
                activeRooms.stream().map(rr -> rr.getRoom().getId()).toList(),
                RoomStatus.AVAILABLE);

        log.info("Reservation checked out: id={}", id);
        return reservationMapper.toDto(reservation);
    }

    @Override
    @Transactional
    public ReservationResponseDto cancel(Long id) {
        Reservations reservation = findById(id);

        if (!BLOCKING_STATUSES.contains(reservation.getStatus())) {
            throw new IllegalArgumentException(
                    "Cannot cancel. Reservation status is: " + reservation.getStatus());
        }

        ReservationStatus previousStatus = reservation.getStatus();
        reservation.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);

        // Restore room availability for both CONFIRMED (RESERVED) and CHECKED_IN (OCCUPIED)
        List<ReservationRooms> activeRooms = reservation.getActiveRooms();
        activeRooms.forEach(rr -> rr.getRoom().setRoomStatus(RoomStatus.AVAILABLE));
        roomRepository.updateStatusByIds(
                activeRooms.stream().map(rr -> rr.getRoom().getId()).toList(),
                RoomStatus.AVAILABLE);

        log.info("Reservation cancelled: id={}, was={}", id, previousStatus);
        return reservationMapper.toDto(reservation);
    }

    @Override
    @Transactional
    public ReservationResponseDto addPayment(Long id, PaymentRequestDto request) {
        Reservations reservation = findById(id);

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

        reservation.setPaidAmount(newPaidAmount);
        reservation.setBalanceAmount(reservation.getTotalAmount().subtract(newPaidAmount));
        reservation.setPaymentStatus(resolvePaymentStatus(newPaidAmount, reservation.getTotalAmount()));
        reservationRepository.save(reservation);

        log.info("Payment added to reservation id={}: amount={}, paymentStatus={}",
                id, request.getAmount(), reservation.getPaymentStatus());
        return reservationMapper.toDto(reservation);
    }

    @Override
    @Transactional(readOnly = true)
    public InvoiceResponseDto getInvoice(Long id) {
        Reservations reservation = findById(id);
        var guest = reservation.getGuest();

        long nights = ChronoUnit.DAYS.between(
                reservation.getCheckInDate().toLocalDate(),
                reservation.getCheckOutDate().toLocalDate());

        List<InvoiceResponseDto.RoomLineDto> roomLines = reservation.getActiveRooms().stream()
                .map(rr -> {
                    var room = rr.getRoom();
                    var promotion = rr.getPromotion();
                    return InvoiceResponseDto.RoomLineDto.builder()
                            .roomNumber(room.getRoomNumber())
                            .roomTypeName(room.getRoomTypes().getTypeName())
                            .nights(nights)
                            .ratePerNight(room.getRoomTypes().getPrice())
                            .basePrice(rr.getBasePrice())
                            .promotionName(promotion != null ? promotion.getName() : null)
                            .discountAmount(rr.getDiscountAmount())
                            .finalPrice(rr.getFinalPrice())
                            .build();
                })
                .toList();

        List<InvoiceResponseDto.ServiceLineDto> serviceLines = reservation.getActiveServiceUsages().stream()
                .map(su -> InvoiceResponseDto.ServiceLineDto.builder()
                        .serviceName(su.getService().getName())
                        .quantity(su.getQuantity())
                        .unitPrice(su.getUnitPrice())
                        .totalPrice(su.getTotalPrice())
                        .build())
                .toList();

        return InvoiceResponseDto.builder()
                .reservationId(reservation.getId())
                .generatedAt(LocalDateTime.now())
                .guestName(guest.getFirstName() + " " + guest.getLastName())
                .guestEmail(guest.getEmail())
                .guestPhone(guest.getPhoneNumber())
                .checkInDate(reservation.getCheckInDate())
                .checkOutDate(reservation.getCheckOutDate())
                .nights(nights)
                .reservationStatus(reservation.getStatus().name())
                .notes(reservation.getNotes())
                .roomCharges(roomLines)
                .roomChargeTotal(roomLines.stream()
                        .map(InvoiceResponseDto.RoomLineDto::getFinalPrice)
                        .reduce(BigDecimal.ZERO, BigDecimal::add))
                .serviceCharges(serviceLines)
                .serviceChargeTotal(reservation.getServiceChargeTotal())
                .totalAmount(reservation.getTotalAmount())
                .paidAmount(reservation.getPaidAmount())
                .balanceAmount(reservation.getBalanceAmount())
                .paymentStatus(reservation.getPaymentStatus().name())
                .build();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Reservations reservation = findById(id);
        reservation.setIsActive(false);
        reservationRepository.save(reservation);
        log.info("Reservation deactivated: id={}", id);
    }

    // ── Helpers ────────────────────────────────────────────────────────────────

    private Reservations findById(Long id) {
        return reservationRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", id));
    }

    private void validateDates(LocalDateTime checkIn, LocalDateTime checkOut) {
        if (!checkOut.isAfter(checkIn)) {
            throw new IllegalArgumentException("Check-out date must be after check-in date");
        }
        if (checkIn.isBefore(LocalDateTime.now().toLocalDate().atStartOfDay())) {
            throw new IllegalArgumentException("Check-in date cannot be in the past");
        }
    }

    private void validatePromotion(Promotions promotion, Rooms room, LocalDateTime checkInDate) {
        if (!Boolean.TRUE.equals(promotion.getIsActive())) {
            throw new IllegalArgumentException("Promotion '" + promotion.getName() + "' is not active");
        }
        if (promotion.getEffectiveDate() != null && checkInDate.isBefore(promotion.getEffectiveDate())) {
            throw new IllegalArgumentException("Promotion '" + promotion.getName() + "' is not yet effective");
        }
        if (promotion.getExpireDate() != null && checkInDate.isAfter(promotion.getExpireDate())) {
            throw new IllegalArgumentException("Promotion '" + promotion.getName() + "' has expired");
        }
        if (!promotionRoomTypeRepository.existsByPromotionAndRoomTypeAndIsActiveTrue(promotion, room.getRoomTypes())) {
            throw new IllegalArgumentException(
                    "Promotion '" + promotion.getName() + "' does not apply to room type '"
                            + room.getRoomTypes().getTypeName() + "'");
        }
    }

    private BigDecimal calculateDiscount(BigDecimal basePrice, Promotions promotion) {
        if ("PERCENTAGE".equalsIgnoreCase(promotion.getDiscountType())) {
            return basePrice.multiply(promotion.getDiscountValue())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        }
        if ("FIXED".equalsIgnoreCase(promotion.getDiscountType())) {
            return promotion.getDiscountValue().min(basePrice);
        }
        return BigDecimal.ZERO;
    }

    private PaymentStatus resolvePaymentStatus(BigDecimal paid, BigDecimal total) {
        if (paid.compareTo(BigDecimal.ZERO) == 0) return PaymentStatus.UNPAID;
        if (paid.compareTo(total) >= 0) return PaymentStatus.PAID;
        return PaymentStatus.PARTIAL;
    }
}
