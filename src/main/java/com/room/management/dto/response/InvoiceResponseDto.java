package com.room.management.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InvoiceResponseDto {

    private Long reservationId;
    private LocalDateTime generatedAt;

    // Guest
    private String guestName;
    private String guestEmail;
    private String guestPhone;

    // Stay
    private LocalDateTime checkInDate;
    private LocalDateTime checkOutDate;
    private Long nights;
    private String reservationStatus;
    private String notes;

    // Room charges
    private List<RoomLineDto> roomCharges;
    private BigDecimal roomChargeTotal;

    // Service charges
    private List<ServiceLineDto> serviceCharges;
    private BigDecimal serviceChargeTotal;

    // Payment history
    private List<PaymentLineDto> payments;

    // Payment summary
    private BigDecimal totalAmount;
    private BigDecimal paidAmount;
    private BigDecimal balanceAmount;
    private String paymentStatus;

    @Data
    @Builder
    public static class RoomLineDto {
        private String roomNumber;
        private String roomTypeName;
        private Long nights;
        private BigDecimal ratePerNight;
        private BigDecimal basePrice;
        private String promotionName;
        private BigDecimal discountAmount;
        private BigDecimal finalPrice;
    }

    @Data
    @Builder
    public static class ServiceLineDto {
        private String serviceName;
        private int quantity;
        private BigDecimal unitPrice;
        private BigDecimal totalPrice;
    }

    @Data
    @Builder
    public static class PaymentLineDto {
        private Long id;
        private String paymentMethod;
        private String paymentType;
        private BigDecimal amount;
        private LocalDateTime paymentDate;
    }
}
