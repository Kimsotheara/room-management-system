package com.room.management.mapper;

import com.room.management.dto.response.PromotionRoomTypeResponseDto;
import com.room.management.entity.room.PromotionRoomType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PromotionRoomTypeMapper {

    @Mapping(source = "promotion.id", target = "promotionId")
    @Mapping(source = "promotion.name", target = "promotionName")
    @Mapping(source = "roomType.id", target = "roomTypeId")
    @Mapping(source = "roomType.typeName", target = "roomTypeName")
    PromotionRoomTypeResponseDto toDto(PromotionRoomType promotionRoomType);

    default List<PromotionRoomTypeResponseDto> toDtoList(List<PromotionRoomType> list) {
        return list.stream().map(this::toDto).toList();
    }
}
