package com.room.management.service.impl;

import com.room.management.dto.request.CreateGuestRequestDto;
import com.room.management.dto.request.GuestRequestDto;
import com.room.management.dto.request.PageAbleRequest;
import com.room.management.dto.request.UpdateGuestRequestDto;
import com.room.management.dto.response.GuestResponseDto;
import com.room.management.dto.response.PageAbleResponse;
import com.room.management.entity.room.Guests;
import com.room.management.exception.DuplicateResourceException;
import com.room.management.exception.ResourceNotFoundException;
import com.room.management.mapper.GuestMapper;
import com.room.management.repository.GuestRepository;
import com.room.management.service.GuestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Base64;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GuestServiceImpl implements GuestService {

    private final GuestRepository guestRepository;
    private final GuestMapper guestMapper;

    @Override
    @Transactional(readOnly = true)
    public List<GuestResponseDto> getAll() {
        return guestRepository.findAll().stream()
                .map(guestMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PageAbleResponse<Guests, GuestResponseDto, Void> getGuestsWithFilter(PageAbleRequest<GuestRequestDto> request) {
        log.info("Fetching guests with filter and pagination");
        Page<Guests> page = guestRepository.findAllWithFilter(request);
        return PageAbleResponse.withoutAddition(page, guestMapper.toPagingGuest(page.getContent()));
    }

    @Override
    @Transactional(readOnly = true)
    public GuestResponseDto getById(Long id) {
        return guestMapper.toDto(findGuestById(id));
    }

    @Override
    @Transactional
    public GuestResponseDto create(CreateGuestRequestDto request) {
        if (StringUtils.hasText(request.getEmail())
                && guestRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Guest", "email", request.getEmail());
        }
        if (StringUtils.hasText(request.getIdentityNumber())
                && guestRepository.existsByIdentityNumber(request.getIdentityNumber())) {
            throw new DuplicateResourceException("Guest", "identityNumber", request.getIdentityNumber());
        }

        Guests guest = guestMapper.toEntity(request);
        guest.setIsActive(true);
        if (StringUtils.hasText(request.getProfileImage())) {
            guest.setProfileImage(Base64.getDecoder().decode(request.getProfileImage()));
        }

        Guests saved = guestRepository.save(guest);
        log.info("Guest created: {} {}", saved.getFirstName(), saved.getLastName());
        return guestMapper.toDto(saved);
    }

    @Override
    @Transactional
    public GuestResponseDto update(Long id, UpdateGuestRequestDto request) {
        Guests guest = findGuestById(id);

        if (StringUtils.hasText(request.getEmail())
                && !request.getEmail().equals(guest.getEmail())
                && guestRepository.existsByEmailAndIdNot(request.getEmail(), id)) {
            throw new DuplicateResourceException("Guest", "email", request.getEmail());
        }
        if (StringUtils.hasText(request.getIdentityNumber())
                && !request.getIdentityNumber().equals(guest.getIdentityNumber())
                && guestRepository.existsByIdentityNumberAndIdNot(request.getIdentityNumber(), id)) {
            throw new DuplicateResourceException("Guest", "identityNumber", request.getIdentityNumber());
        }

        guestMapper.updateEntity(request, guest);
        if (StringUtils.hasText(request.getProfileImage())) {
            guest.setProfileImage(Base64.getDecoder().decode(request.getProfileImage()));
        }

        Guests saved = guestRepository.save(guest);
        log.info("Guest updated: {} {}", saved.getFirstName(), saved.getLastName());
        return guestMapper.toDto(saved);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Guests guest = findGuestById(id);
        guest.setIsActive(false);
        guestRepository.save(guest);
        log.info("Guest deactivated: {} {}", guest.getFirstName(), guest.getLastName());
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] getProfileImage(Long id) {
        return guestRepository.findProfileImageById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Guest", id));
    }

    private Guests findGuestById(Long id) {
        return guestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Guest", id));
    }
}
