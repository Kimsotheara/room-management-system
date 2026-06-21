package com.room.management.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentRequestDto {

    @NotNull(message = "Reservation ID is required")
    private Long reservationId;

    @NotBlank(message = "Payment method is required")
    private String paymentMethod; // cash, card, transfer

    @NotBlank(message = "Payment type is required")
    private String paymentType; // deposit, final

    @NotNull(message = "Payment amount is required")
    @DecimalMin(value = "0.01", message = "Payment amount must be greater than 0")
    private BigDecimal amount;
}
