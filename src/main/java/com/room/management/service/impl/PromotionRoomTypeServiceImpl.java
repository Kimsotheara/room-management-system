package com.room.management.service.impl;

import com.room.management.dto.request.AssignPromotionRoomTypeRequestDto;
import com.room.management.dto.response.PromotionRoomTypeResponseDto;
import com.room.management.entity.room.PromotionRoomType;
import com.room.management.entity.room.Promotions;
import com.room.management.entity.room.RoomTypes;
import com.room.management.exception.ResourceNotFoundException;
import com.room.management.mapper.PromotionRoomTypeMapper;
import com.room.management.repository.PromotionRepository;
import com.room.management.repository.PromotionRoomTypeRepository;
import com.room.management.repository.RoomTypeRepository;
import com.room.management.service.PromotionRoomTypeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PromotionRoomTypeServiceImpl implements PromotionRoomTypeService {

    private final PromotionRoomTypeRepository promotionRoomTypeRepository;
    private final PromotionRepository promotionRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final PromotionRoomTypeMapper promotionRoomTypeMapper;

    @Override
    @Transactional(readOnly = true)
    public List<PromotionRoomTypeResponseDto> getByPromotionId(Long promotionId) {
        if (!promotionRepository.existsById(promotionId)) {
            throw new ResourceNotFoundException("Promotion", promotionId);
        }
        return promotionRoomTypeMapper.toDtoList(
                promotionRoomTypeRepository.findActiveByPromotionId(promotionId));
    }

    @Override
    @Transactional(readOnly = true)
    public PromotionRoomTypeResponseDto getById(Long id) {
        return promotionRoomTypeMapper.toDto(findByIdWithDetails(id));
    }

    @Override
    @Transactional
    public List<PromotionRoomTypeResponseDto> assign(AssignPromotionRoomTypeRequestDto request) {
        Promotions promotion = promotionRepository.findById(request.getPromotionId())
                .orElseThrow(() -> new ResourceNotFoundException("Promotion", request.getPromotionId()));

        List<PromotionRoomType> toSave = new ArrayList<>();
        for (Long roomTypeId : request.getRoomTypeIds()) {
            RoomTypes roomType = roomTypeRepository.findById(roomTypeId)
                    .orElseThrow(() -> new ResourceNotFoundException("RoomType", roomTypeId));

            // Skip if already assigned
            if (promotionRoomTypeRepository.existsByPromotionAndRoomTypeAndIsActiveTrue(promotion, roomType)) {
                continue;
            }

            PromotionRoomType prt = new PromotionRoomType();
            prt.setPromotion(promotion);
            prt.setRoomType(roomType);
            prt.setIsActive(true);
            toSave.add(prt);
        }

        promotionRoomTypeRepository.saveAll(toSave);
        log.info("{} room type(s) assigned to promotion: {}", toSave.size(), promotion.getName());

        return promotionRoomTypeMapper.toDtoList(
                promotionRoomTypeRepository.findActiveByPromotionId(request.getPromotionId()));
    }

    @Override
    @Transactional
    public void remove(Long id) {
        PromotionRoomType prt = findByIdWithDetails(id);

        if (!Boolean.TRUE.equals(prt.getIsActive())) {
            throw new IllegalArgumentException("Promotion room type is already removed");
        }

        prt.setIsActive(false);
        promotionRoomTypeRepository.save(prt);
        log.info("PromotionRoomType id={} removed (promotion='{}', roomType='{}')",
                id, prt.getPromotion().getName(), prt.getRoomType().getTypeName());
    }

    // ── Helpers ────────────────────────────────────────────────────────────────

    private PromotionRoomType findByIdWithDetails(Long id) {
        return promotionRoomTypeRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("PromotionRoomType", id));
    }
}
