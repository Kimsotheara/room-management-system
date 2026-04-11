package com.room.management.service.impl;

import com.room.management.dto.request.CreateRoomRequestDto;
import com.room.management.dto.request.UpdateRoomRequestDto;
import com.room.management.dto.response.RoomResponseDto;
import com.room.management.entity.room.RoomImages;
import com.room.management.entity.room.RoomTypes;
import com.room.management.entity.room.Rooms;
import com.room.management.enums.RoomStatus;
import com.room.management.exception.DuplicateResourceException;
import com.room.management.exception.ResourceNotFoundException;
import com.room.management.mapper.RoomMapper;
import com.room.management.repository.RoomImageRepository;
import com.room.management.repository.RoomRepository;
import com.room.management.repository.RoomTypeRepository;
import com.room.management.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final RoomImageRepository roomImageRepository;
    private final RoomMapper roomMapper;

    @Override
    @Transactional(readOnly = true)
    public List<RoomResponseDto> getAll() {
        return roomRepository.findAll().stream()
                .map(roomMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public RoomResponseDto getById(Long id) {
        return roomMapper.toDto(findRoomById(id));
    }

    @Override
    @Transactional
    public RoomResponseDto create(CreateRoomRequestDto request) {
        if (roomRepository.existsByRoomNumber(request.getRoomNumber())) {
            throw new DuplicateResourceException("Room", "roomNumber", request.getRoomNumber());
        }

        RoomTypes roomType = findRoomTypeById(request.getRoomTypeId());

        Rooms room = roomMapper.toEntity(request);
        room.setRoomTypes(roomType);
        room.setRoomStatus(parseRoomStatus(request.getRoomStatus(), RoomStatus.AVAILABLE));
        room.setIsActive(true);

        Rooms saved = roomRepository.save(room);
        log.info("Room created: {}", saved.getRoomNumber());
        return roomMapper.toDto(saved);
    }

    @Override
    @Transactional
    public RoomResponseDto update(Long id, UpdateRoomRequestDto request) {
        Rooms room = findRoomById(id);

        if (StringUtils.hasText(request.getRoomNumber())
                && !request.getRoomNumber().equals(room.getRoomNumber())
                && roomRepository.existsByRoomNumberAndIdNot(request.getRoomNumber(), id)) {
            throw new DuplicateResourceException("Room", "roomNumber", request.getRoomNumber());
        }

        if (request.getRoomTypeId() != null) {
            room.setRoomTypes(findRoomTypeById(request.getRoomTypeId()));
        }

        roomMapper.updateEntity(request, room);

        if (StringUtils.hasText(request.getRoomStatus())) {
            room.setRoomStatus(parseRoomStatus(request.getRoomStatus(), null));
        }

        Rooms saved = roomRepository.save(room);
        log.info("Room updated: {}", saved.getRoomNumber());
        return roomMapper.toDto(saved);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Rooms room = findRoomById(id);
        room.setIsActive(false);
        roomRepository.save(room);
        log.info("Room deactivated: {}", room.getRoomNumber());
    }

    @Override
    @Transactional
    public RoomResponseDto addImages(Long roomId, List<String> images) {
        Rooms room = findRoomById(roomId);
        int nextOrder = roomImageRepository.countByRoomIdAndIsActiveTrue(roomId);

        List<RoomImages> roomImages = new ArrayList<>();
        for (int i = 0; i < images.size(); i++) {
            RoomImages image = new RoomImages();
            image.setRoom(room);
            image.setImageData(Base64.getDecoder().decode(images.get(i)));
            image.setDisplayOrder(nextOrder + i);
            image.setIsActive(true);
            roomImages.add(image);
        }
        roomImageRepository.saveAll(roomImages);

        log.info("{} image(s) added to room: {}", images.size(), room.getRoomNumber());
        return roomMapper.toDto(findRoomById(roomId));
    }

    @Override
    @Transactional
    public void deleteImage(Long roomId, Long imageId) {
        findRoomById(roomId);

        RoomImages image = roomImageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("RoomImage", imageId));

        image.setIsActive(false);
        roomImageRepository.save(image);
        log.info("Image {} removed from room {}", imageId, roomId);
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] getImage(Long roomId, Long imageId) {
        return roomImageRepository.findImageDataById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("RoomImage", imageId));
    }

    private RoomStatus parseRoomStatus(String value, RoomStatus defaultStatus) {
        if (!StringUtils.hasText(value)) return defaultStatus;
        try {
            return RoomStatus.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid room status: '" + value + "'. Valid values: AVAILABLE, RESERVED, OCCUPIED, MAINTENANCE");
        }
    }

    private Rooms findRoomById(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room", id));
    }

    private RoomTypes findRoomTypeById(Long id) {
        return roomTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RoomType", id));
    }
}
