package com.room.management.mapper;

import com.room.management.dto.request.CreateRoomTypeRequestDto;
import com.room.management.dto.request.UpdateRoomTypeRequestDto;
import com.room.management.dto.response.RoomTypeResponseDto;
import com.room.management.entity.room.RoomTypes;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RoomTypeMapper {

    @Mapping(source = "id", target = "roomTypeId")
    RoomTypeResponseDto toDto(RoomTypes roomType);

    @Mapping(target = "isActive", ignore = true)
    RoomTypes toEntity(CreateRoomTypeRequestDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(UpdateRoomTypeRequestDto dto, @MappingTarget RoomTypes roomType);
}
