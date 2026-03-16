package com.room.management.mapper;

import com.room.management.dto.request.CreatePromotionRequestDto;
import com.room.management.dto.request.UpdatePromotionRequestDto;
import com.room.management.dto.response.PromotionResponseDto;
import com.room.management.entity.room.Promotions;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PromotionMapper {

    PromotionResponseDto toDto(Promotions promotions);

    @Mapping(target = "isActive", ignore = true)
    Promotions toEntity(CreatePromotionRequestDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(UpdatePromotionRequestDto dto, @MappingTarget Promotions promotions);

}
