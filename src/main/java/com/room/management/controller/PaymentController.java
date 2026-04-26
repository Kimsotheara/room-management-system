package com.room.management.controller;

import com.room.management.annotation.AuthResource;
import com.room.management.dto.request.PaymentRequestDto;
import com.room.management.dto.response.ApiResponse;
import com.room.management.dto.response.PaymentResponseDto;
import com.room.management.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Payment Management", description = "Record and manage reservation payments")
@AuthResource(value = "payment-management", category = "PAYMENT")
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/reservation/{reservationId}")
    @AuthResource(value = "list-payments", description = "List all payments for a reservation")
    @Operation(summary = "List payments by reservation")
    public ResponseEntity<ApiResponse<List<PaymentResponseDto>>> getByReservationId(
            @PathVariable Long reservationId) {
        return ResponseEntity.ok(ApiResponse.success(paymentService.getByReservationId(reservationId)));
    }

    @GetMapping("/{id}")
    @AuthResource(value = "get-payment", description = "Get payment by ID")
    @Operation(summary = "Get payment by ID")
    public ResponseEntity<ApiResponse<PaymentResponseDto>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(paymentService.getById(id)));
    }

    @PostMapping
    @AuthResource(value = "add-payment", description = "Record a payment for a reservation")
    @Operation(summary = "Add payment", description = "Records a payment and auto-updates paymentStatus (UNPAID → PARTIAL → PAID).")
    public ResponseEntity<ApiResponse<PaymentResponseDto>> add(
            @Valid @RequestBody PaymentRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Payment recorded successfully", paymentService.add(request)));
    }

    @DeleteMapping("/{id}")
    @AuthResource(value = "delete-payment", description = "Void a payment and reverse the reservation balance")
    @Operation(summary = "Delete payment", description = "Soft deletes a payment and subtracts its amount from the reservation paid amount.")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        paymentService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Payment deleted successfully", null));
    }
}
