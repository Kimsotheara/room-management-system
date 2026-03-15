package com.room.management.service.impl;

import com.room.management.dto.request.CreateHotelRequestDto;
import com.room.management.dto.request.UpdateHotelRequestDto;
import com.room.management.dto.response.HotelResponseDto;
import com.room.management.entity.room.Hotels;
import com.room.management.exception.DuplicateResourceException;
import com.room.management.exception.ResourceNotFoundException;
import com.room.management.mapper.HotelMapper;
import com.room.management.repository.HotelRepository;
import com.room.management.service.HotelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class HotelServiceImpl implements HotelService {

    private final HotelRepository hotelRepository;
    private final HotelMapper hotelMapper;

    @Override
    @Transactional(readOnly = true)
    public List<HotelResponseDto> getAll() {
        return hotelRepository.findAll().stream()
                .map(hotelMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public HotelResponseDto getById(Long id) {
        return hotelMapper.toDto(findHotelById(id));
    }

    @Override
    @Transactional
    public HotelResponseDto create(CreateHotelRequestDto request) {
        if (hotelRepository.existsByHotelName(request.getHotelName())) {
            throw new DuplicateResourceException("Hotel", "hotelName", request.getHotelName());
        }

        Hotels hotel = hotelMapper.toEntity(request);
        hotel.setIsActive(true);

        Hotels saved = hotelRepository.save(hotel);
        log.info("Hotel created: {}", saved.getHotelName());
        return hotelMapper.toDto(saved);
    }

    @Override
    @Transactional
    public HotelResponseDto update(Long id, UpdateHotelRequestDto request) {
        Hotels hotel = findHotelById(id);

        if (StringUtils.hasText(request.getHotelName())
                && !request.getHotelName().equals(hotel.getHotelName())
                && hotelRepository.existsByHotelName(request.getHotelName())) {
            throw new DuplicateResourceException("Hotel", "hotelName", request.getHotelName());
        }

        hotelMapper.updateEntity(request, hotel);

        Hotels saved = hotelRepository.save(hotel);
        log.info("Hotel updated: {}", saved.getHotelName());
        return hotelMapper.toDto(saved);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Hotels hotel = findHotelById(id);
        hotel.setIsActive(false);
        hotelRepository.save(hotel);
        log.info("Hotel deactivated: {}", hotel.getHotelName());
    }

    private Hotels findHotelById(Long id) {
        return hotelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel", id));
    }
}
