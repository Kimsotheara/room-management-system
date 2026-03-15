package com.room.management.service.impl;

import com.room.management.dto.request.CreateRoomTypeRequestDto;
import com.room.management.dto.request.UpdateRoomTypeRequestDto;
import com.room.management.dto.response.RoomTypeResponseDto;
import com.room.management.entity.room.RoomTypes;
import com.room.management.exception.DuplicateResourceException;
import com.room.management.exception.ResourceNotFoundException;
import com.room.management.mapper.RoomTypeMapper;
import com.room.management.repository.RoomTypeRepository;
import com.room.management.service.RoomTypeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoomTypeServiceImpl implements RoomTypeService {

    private final RoomTypeRepository roomTypeRepository;
    private final RoomTypeMapper roomTypeMapper;

    @Override
    @Transactional(readOnly = true)
    public List<RoomTypeResponseDto> getAll() {
        return roomTypeRepository.findAll().stream()
                .map(roomTypeMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public RoomTypeResponseDto getById(Long id) {
        return roomTypeMapper.toDto(findRoomTypeById(id));
    }

    @Override
    @Transactional
    public RoomTypeResponseDto create(CreateRoomTypeRequestDto request) {
        if (roomTypeRepository.existsByTypeName(request.getTypeName())) {
            throw new DuplicateResourceException("RoomType", "typeName", request.getTypeName());
        }

        RoomTypes roomType = roomTypeMapper.toEntity(request);
        roomType.setIsActive(true);

        RoomTypes saved = roomTypeRepository.save(roomType);
        log.info("RoomType created: {}", saved.getTypeName());
        return roomTypeMapper.toDto(saved);
    }

    @Override
    @Transactional
    public RoomTypeResponseDto update(Long id, UpdateRoomTypeRequestDto request) {
        RoomTypes roomType = findRoomTypeById(id);

        if (StringUtils.hasText(request.getTypeName())
                && !request.getTypeName().equals(roomType.getTypeName())
                && roomTypeRepository.existsByTypeName(request.getTypeName())) {
            throw new DuplicateResourceException("RoomType", "typeName", request.getTypeName());
        }

        roomTypeMapper.updateEntity(request, roomType);

        RoomTypes saved = roomTypeRepository.save(roomType);
        log.info("RoomType updated: {}", saved.getTypeName());
        return roomTypeMapper.toDto(saved);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        RoomTypes roomType = findRoomTypeById(id);
        roomType.setIsActive(false);
        roomTypeRepository.save(roomType);
        log.info("RoomType deactivated: {}", roomType.getTypeName());
    }

    private RoomTypes findRoomTypeById(Long id) {
        return roomTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RoomType", id));
    }
}
