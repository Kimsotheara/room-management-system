package com.room.management.service;

import com.room.management.dto.request.CreatePromotionRequestDto;
import com.room.management.dto.request.UpdatePromotionRequestDto;
import com.room.management.dto.response.PromotionResponseDto;

import java.util.List;

public interface PromotionService {

    List<PromotionResponseDto> getAllPromotions();

    PromotionResponseDto getById(Long id);

    PromotionResponseDto create(CreatePromotionRequestDto request);

    PromotionResponseDto update(Long id, UpdatePromotionRequestDto request);

    void delete(Long id);
}
