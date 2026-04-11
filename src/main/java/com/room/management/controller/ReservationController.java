package com.room.management.controller;

import com.room.management.annotation.AuthResource;
import com.room.management.dto.request.CreateReservationRequestDto;
import com.room.management.dto.request.PaymentRequestDto;
import com.room.management.dto.request.UpdateReservationRequestDto;
import com.room.management.dto.response.ApiResponse;
import com.room.management.dto.response.InvoiceResponseDto;
import com.room.management.dto.response.ReservationResponseDto;
import com.room.management.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
@Tag(name = "Reservation Management", description = "Booking, check-in, check-out and payment operations")
@AuthResource(value = "reservation-management", category = "RESERVATION")
public class ReservationController {

    private final ReservationService reservationService;

    @GetMapping
    @AuthResource(value = "list-reservations", description = "List all reservations")
    @Operation(summary = "List all reservations")
    public ResponseEntity<ApiResponse<List<ReservationResponseDto>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(reservationService.getAll()));
    }

    @GetMapping("/{id}")
    @AuthResource(value = "get-reservation", description = "Get reservation by ID")
    @Operation(summary = "Get reservation by ID")
    public ResponseEntity<ApiResponse<ReservationResponseDto>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(reservationService.getById(id)));
    }

    @PostMapping
    @AuthResource(value = "create-reservation", description = "Create a new reservation")
    @Operation(summary = "Create reservation", description = "Books one or more rooms for a guest. Validates availability, calculates prices and applies promotions.")
    public ResponseEntity<ApiResponse<ReservationResponseDto>> create(@Valid @RequestBody CreateReservationRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Reservation created successfully", reservationService.create(request)));
    }

    @PutMapping("/{id}")
    @AuthResource(value = "update-reservation", description = "Update reservation notes")
    @Operation(summary = "Update reservation")
    public ResponseEntity<ApiResponse<ReservationResponseDto>> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateReservationRequestDto request) {
        return ResponseEntity.ok(ApiResponse.success("Reservation updated successfully", reservationService.update(id, request)));
    }

    @PostMapping("/{id}/check-in")
    @AuthResource(value = "checkin-reservation", description = "Check in a confirmed reservation")
    @Operation(summary = "Check in", description = "Transitions status CONFIRMED → CHECKED_IN and marks all rooms as OCCUPIED.")
    public ResponseEntity<ApiResponse<ReservationResponseDto>> checkIn(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Checked in successfully", reservationService.checkIn(id)));
    }

    @PostMapping("/{id}/check-out")
    @AuthResource(value = "checkout-reservation", description = "Check out an active reservation")
    @Operation(summary = "Check out", description = "Transitions status CHECKED_IN → CHECKED_OUT and marks all rooms as AVAILABLE.")
    public ResponseEntity<ApiResponse<ReservationResponseDto>> checkOut(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Checked out successfully", reservationService.checkOut(id)));
    }

    @PostMapping("/{id}/cancel")
    @AuthResource(value = "cancel-reservation", description = "Cancel a reservation")
    @Operation(summary = "Cancel reservation", description = "Cancels a CONFIRMED or CHECKED_IN reservation.")
    public ResponseEntity<ApiResponse<ReservationResponseDto>> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Reservation cancelled successfully", reservationService.cancel(id)));
    }

    @PostMapping("/{id}/payments")
    @AuthResource(value = "add-reservation-payment", description = "Add payment to a reservation")
    @Operation(summary = "Add payment", description = "Adds a payment amount and auto-updates paymentStatus (UNPAID → PARTIAL → PAID).")
    public ResponseEntity<ApiResponse<ReservationResponseDto>> addPayment(
            @PathVariable Long id,
            @Valid @RequestBody PaymentRequestDto request) {
        return ResponseEntity.ok(ApiResponse.success("Payment recorded successfully", reservationService.addPayment(id, request)));
    }

    @GetMapping("/{id}/invoice")
    @AuthResource(value = "get-reservation-invoice", description = "Generate invoice for a reservation")
    @Operation(summary = "Get invoice", description = "Returns a full invoice with room charges, service charges, and payment summary.")
    public ResponseEntity<ApiResponse<InvoiceResponseDto>> getInvoice(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(reservationService.getInvoice(id)));
    }

    @DeleteMapping("/{id}")
    @AuthResource(value = "delete-reservation", description = "Soft delete a reservation")
    @Operation(summary = "Delete reservation (soft delete)")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        reservationService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Reservation deleted successfully", null));
    }
}
