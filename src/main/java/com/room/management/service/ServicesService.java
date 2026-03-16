package com.room.management.service;

import com.room.management.dto.request.*;
import com.room.management.dto.response.ServiceResponseDto;

import java.util.List;

public interface ServicesService {

    List<ServiceResponseDto> getAllServices();

    ServiceResponseDto getById(Long id);

    ServiceResponseDto create(CreateServiceRequestDto request);

    ServiceResponseDto update(Long id, UpdateServiceRequestDto request);

    void delete(Long id);
}
