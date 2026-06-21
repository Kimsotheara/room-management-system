package com.room.management.service;

import com.room.management.dto.request.PaymentRequestDto;
import com.room.management.dto.response.PaymentResponseDto;

import java.util.List;

public interface PaymentService {

    List<PaymentResponseDto> getByReservationId(Long reservationId);

    PaymentResponseDto getById(Long id);

    PaymentResponseDto add(PaymentRequestDto request);

    void delete(Long id);
}
