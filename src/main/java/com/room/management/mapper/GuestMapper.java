package com.room.management.mapper;

import com.room.management.dto.request.CreateGuestRequestDto;
import com.room.management.dto.request.UpdateGuestRequestDto;
import com.room.management.dto.response.GuestResponseDto;
import com.room.management.entity.room.Guests;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GuestMapper {

    @Mapping(source = "id", target = "guestId")
    GuestResponseDto toDto(Guests guest);

    @Mapping(target = "isActive", ignore = true)
    Guests toEntity(CreateGuestRequestDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(UpdateGuestRequestDto dto, @MappingTarget Guests guest);
}
