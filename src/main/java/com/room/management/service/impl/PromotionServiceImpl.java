package com.room.management.service.impl;

import com.room.management.dto.request.CreatePromotionRequestDto;
import com.room.management.dto.request.UpdatePromotionRequestDto;
import com.room.management.dto.response.PromotionResponseDto;
import com.room.management.entity.room.Promotions;
import com.room.management.exception.DuplicateResourceException;
import com.room.management.exception.ResourceNotFoundException;
import com.room.management.mapper.PromotionMapper;
import com.room.management.repository.PromotionRepository;
import com.room.management.service.PromotionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PromotionServiceImpl implements PromotionService {

    private final PromotionRepository promotionRepository;
    private final PromotionMapper promotionMapper;

    @Override
    @Transactional(readOnly = true)
    public List<PromotionResponseDto> getAllPromotions() {
        return promotionRepository.findAll().stream()
                .map(promotionMapper::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PromotionResponseDto getById(Long id) {
        return promotionMapper.toDto(findById(id));
    }

    @Override
    @Transactional
    public PromotionResponseDto create(CreatePromotionRequestDto request) {
        if (promotionRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Promotion", "name", request.getName());
        }

        Promotions obj = promotionRepository.save(promotionMapper.toEntity(request));
        log.info("Promotion created: {}", obj.getName());
        return promotionMapper.toDto(obj);
    }

    @Override
    @Transactional
    public PromotionResponseDto update(Long id, UpdatePromotionRequestDto request) {
        Promotions obj = findById(id);

        if (StringUtils.hasText(request.getName())
                && !request.getName().equals(obj.getName())
                && promotionRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Promotion", "name", request.getName());
        }

        promotionMapper.updateEntity(request, obj);
        Promotions updated = promotionRepository.save(obj);
        log.info("Promotion updated: {}", updated.getName());
        return promotionMapper.toDto(updated);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Promotions promotion = findById(id);
        promotion.setIsActive(false);
        promotionRepository.save(promotion);
        log.info("Promotion deactivated: {}", promotion.getName());
    }

    // ── Helpers ────────────────────────────────────────────────────────────────

    private Promotions findById(Long id) {
        return promotionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promotion", id));
    }
}
