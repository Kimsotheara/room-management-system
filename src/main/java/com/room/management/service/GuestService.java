package com.room.management.service;

import com.room.management.dto.request.CreateGuestRequestDto;
import com.room.management.dto.request.GuestRequestDto;
import com.room.management.dto.request.PageAbleRequest;
import com.room.management.dto.request.UpdateGuestRequestDto;
import com.room.management.dto.response.GuestResponseDto;
import com.room.management.dto.response.PageAbleResponse;
import com.room.management.entity.room.Guests;

import java.util.List;

public interface GuestService {

    List<GuestResponseDto> getAll();

    PageAbleResponse<Guests, GuestResponseDto, Void> getGuestsWithFilter(PageAbleRequest<GuestRequestDto> request);

    GuestResponseDto getById(Long id);

    GuestResponseDto create(CreateGuestRequestDto request);

    GuestResponseDto update(Long id, UpdateGuestRequestDto request);

    void delete(Long id);

    byte[] getProfileImage(Long id);
}
