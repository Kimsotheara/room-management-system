package com.room.management.service;

import com.room.management.dto.request.*;
import com.room.management.dto.response.HotelResponseDto;

import java.util.List;

public interface HotelService {

    List<HotelResponseDto> getAll();

    HotelResponseDto getById(Long id);

    HotelResponseDto create(CreateHotelRequestDto request);

    HotelResponseDto update(Long id, UpdateHotelRequestDto request);

    void delete(Long id);
}
