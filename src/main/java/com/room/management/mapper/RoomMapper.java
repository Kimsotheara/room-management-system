package com.room.management.mapper;

import com.room.management.dto.request.CreateRoomRequestDto;
import com.room.management.dto.request.UpdateRoomRequestDto;
import com.room.management.dto.response.RoomResponseDto;
import com.room.management.entity.room.Rooms;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        uses = {RoomTypeMapper.class, RoomImageMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RoomMapper {

    @Mapping(source = "id", target = "roomId")
    @Mapping(source = "roomTypes", target = "roomType")
    @Mapping(source = "activeImages", target = "images")
    RoomResponseDto toDto(Rooms room);

    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "roomTypes", ignore = true)
    @Mapping(target = "roomImages", ignore = true)
    @Mapping(target = "roomStatus", ignore = true)
    Rooms toEntity(CreateRoomRequestDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "roomTypes", ignore = true)
    @Mapping(target = "roomImages", ignore = true)
    @Mapping(target = "roomStatus", ignore = true)
    void updateEntity(UpdateRoomRequestDto dto, @MappingTarget Rooms room);
}
