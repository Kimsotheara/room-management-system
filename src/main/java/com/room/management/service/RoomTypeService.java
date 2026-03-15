package com.room.management.service;

import com.room.management.dto.request.CreateRoomTypeRequestDto;
import com.room.management.dto.request.UpdateRoomTypeRequestDto;
import com.room.management.dto.response.RoomTypeResponseDto;

import java.util.List;

public interface RoomTypeService {

    List<RoomTypeResponseDto> getAll();

    RoomTypeResponseDto getById(Long id);

    RoomTypeResponseDto create(CreateRoomTypeRequestDto request);

    RoomTypeResponseDto update(Long id, UpdateRoomTypeRequestDto request);

    void delete(Long id);
}
