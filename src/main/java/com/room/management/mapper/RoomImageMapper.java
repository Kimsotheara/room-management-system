package com.room.management.mapper;

import com.room.management.dto.response.RoomImageResponseDto;
import com.room.management.entity.room.RoomImages;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class RoomImageMapper {

    @Mapping(source = "id", target = "imageId")
    @Mapping(target = "imageData", ignore = true)
    public abstract RoomImageResponseDto toDto(RoomImages image);

    @AfterMapping
    protected void setImageUrl(RoomImages image, @MappingTarget RoomImageResponseDto.RoomImageResponseDtoBuilder dto) {
        String url = "/api/rooms/" + image.getRoom().getId() + "/images/" + image.getId() + "/file";
        dto.imageData(url);
    }
}
