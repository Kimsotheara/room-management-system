package com.room.management.service;

import com.room.management.dto.request.CreateReservationRequestDto;
import com.room.management.dto.request.UpdateReservationRequestDto;
import com.room.management.dto.response.InvoiceResponseDto;
import com.room.management.dto.response.ReservationResponseDto;

import java.util.List;

public interface ReservationService {

    List<ReservationResponseDto> getAll();

    ReservationResponseDto getById(Long id);

    ReservationResponseDto create(CreateReservationRequestDto request);

    ReservationResponseDto update(Long id, UpdateReservationRequestDto request);

    ReservationResponseDto checkIn(Long id);

    ReservationResponseDto checkOut(Long id);

    ReservationResponseDto cancel(Long id);

    InvoiceResponseDto getInvoice(Long id);

    void delete(Long id);

}
