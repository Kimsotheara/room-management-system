package com.room.management.service;

import com.room.management.dto.request.AddServiceUsageRequestDto;
import com.room.management.dto.response.ServiceUsageResponseDto;

import java.util.List;

public interface ServiceUsageService {

    List<ServiceUsageResponseDto> getByReservationId(Long reservationId);

    ServiceUsageResponseDto getById(Long id);

    ServiceUsageResponseDto add(AddServiceUsageRequestDto request);

    void delete(Long id);
}
