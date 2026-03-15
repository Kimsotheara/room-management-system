package com.room.management.mapper;

import com.room.management.dto.request.CreateHotelRequestDto;
import com.room.management.dto.request.UpdateHotelRequestDto;
import com.room.management.dto.response.HotelResponseDto;
import com.room.management.entity.room.Hotels;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface HotelMapper {

    @Mapping(source = "id", target = "hotelId")
    HotelResponseDto toDto(Hotels hotel);

    @Mapping(target = "isActive", ignore = true)
    Hotels toEntity(CreateHotelRequestDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(UpdateHotelRequestDto dto, @MappingTarget Hotels hotel);
}
