package com.room.management.service;

import com.room.management.dto.request.CreateRoomRequestDto;
import com.room.management.dto.request.UpdateRoomRequestDto;
import com.room.management.dto.response.RoomResponseDto;

import java.util.List;

public interface RoomService {

    List<RoomResponseDto> getAll();

    RoomResponseDto getById(Long id);

    RoomResponseDto create(CreateRoomRequestDto request);

    RoomResponseDto update(Long id, UpdateRoomRequestDto request);

    void delete(Long id);

    RoomResponseDto addImages(Long roomId, List<String> images);

    void deleteImage(Long roomId, Long imageId);

    byte[] getImage(Long roomId, Long imageId);
}
