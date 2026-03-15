package com.room.management.mapper;

import com.room.management.dto.response.RoomImageResponseDto;
import com.room.management.entity.room.RoomImages;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RoomImageMapper {

    @Mapping(source = "id", target = "imageId")
    RoomImageResponseDto toDto(RoomImages image);
}
