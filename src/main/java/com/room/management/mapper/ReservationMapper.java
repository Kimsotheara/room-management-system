package com.room.management.mapper;

import com.room.management.dto.response.ReservationResponseDto;
import com.room.management.entity.room.Reservations;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.temporal.ChronoUnit;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class ReservationMapper {

    @Autowired
    protected ReservationRoomMapper reservationRoomMapper;

    @Autowired
    protected ServiceUsageMapper serviceUsageMapper;

    @Mapping(source = "guest.id", target = "guestId")
    @Mapping(target = "guestName", ignore = true)
    @Mapping(target = "nights", ignore = true)
    @Mapping(target = "rooms", ignore = true)
    @Mapping(target = "serviceUsages", ignore = true)
    public abstract ReservationResponseDto toDto(Reservations reservation);

    @AfterMapping
    protected void setComputedFields(Reservations reservation, @MappingTarget ReservationResponseDto.ReservationResponseDtoBuilder dto) {
        dto.guestName(reservation.getGuest().getFirstName() + " " + reservation.getGuest().getLastName());
        dto.nights(ChronoUnit.DAYS.between(reservation.getCheckInDate().toLocalDate(), reservation.getCheckOutDate().toLocalDate()));
        dto.rooms(reservation.getActiveRooms().stream()
                .map(reservationRoomMapper::toDto)
                .toList());
        dto.serviceUsages(reservation.getActiveServiceUsages().stream()
                .map(serviceUsageMapper::toDto)
                .toList());
    }
}
