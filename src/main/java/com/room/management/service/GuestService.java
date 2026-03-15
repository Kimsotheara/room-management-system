package com.room.management.service;

import com.room.management.dto.request.CreateGuestRequestDto;
import com.room.management.dto.request.UpdateGuestRequestDto;
import com.room.management.dto.response.GuestResponseDto;

import java.util.List;

public interface GuestService {

    List<GuestResponseDto> getAll();

    GuestResponseDto getById(Long id);

    GuestResponseDto create(CreateGuestRequestDto request);

    GuestResponseDto update(Long id, UpdateGuestRequestDto request);

    void delete(Long id);

    GuestResponseDto uploadProfileImage(Long id, String imageData);

    void deleteProfileImage(Long id);
}
