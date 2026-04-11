package com.room.management.service;

import com.room.management.dto.response.PromotionRoomTypeResponseDto;

import java.util.List;

public interface PromotionRoomTypeService {

    List<PromotionRoomTypeResponseDto> getByPromotionId(Long promotionId);

    PromotionRoomTypeResponseDto getById(Long id);

    List<PromotionRoomTypeResponseDto> assign(Long promotionId, List<Long> roomTypeIds);

    void remove(Long id);
}
