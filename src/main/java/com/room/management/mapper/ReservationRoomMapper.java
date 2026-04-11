package com.room.management.mapper;

import com.room.management.dto.response.ReservationRoomResponseDto;
import com.room.management.entity.room.ReservationRooms;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ReservationRoomMapper {

    @Mapping(source = "room.id", target = "roomId")
    @Mapping(source = "room.roomNumber", target = "roomNumber")
    @Mapping(source = "room.roomTypes.typeName", target = "roomTypeName")
    @Mapping(source = "promotion.id", target = "promotionId")
    @Mapping(source = "promotion.name", target = "promotionName")
    ReservationRoomResponseDto toDto(ReservationRooms reservationRoom);
}
