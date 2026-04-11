package com.room.management.mapper;

import com.room.management.dto.request.CreatePromotionRequestDto;
import com.room.management.dto.request.UpdatePromotionRequestDto;
import com.room.management.dto.response.PromotionResponseDto;
import com.room.management.entity.room.Promotions;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class PromotionMapper {

    @Autowired
    protected RoomTypeMapper roomTypeMapper;

    @Mapping(target = "roomTypes", ignore = true)
    public abstract PromotionResponseDto toDto(Promotions promotions);

    @AfterMapping
    protected void setRoomTypes(Promotions promotion, @MappingTarget PromotionResponseDto.PromotionResponseDtoBuilder dto) {
        dto.roomTypes(promotion.getActiveRoomTypes().stream()
                .map(prt -> roomTypeMapper.toDto(prt.getRoomType()))
                .toList());
    }

    @Mapping(target = "isActive", ignore = true)
    public abstract Promotions toEntity(CreatePromotionRequestDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void updateEntity(UpdatePromotionRequestDto dto, @MappingTarget Promotions promotions);
}
