package com.room.management.mapper;

import com.room.management.dto.request.CreateGuestRequestDto;
import com.room.management.dto.request.UpdateGuestRequestDto;
import com.room.management.dto.response.GuestResponseDto;
import com.room.management.entity.room.Guests;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class GuestMapper {

    @Mapping(source = "id", target = "guestId")
    @Mapping(target = "profileImage", ignore = true)
    public abstract GuestResponseDto toDto(Guests guest);

    @AfterMapping
    protected void setProfileImageUrl(Guests guest, @MappingTarget GuestResponseDto.GuestResponseDtoBuilder dto) {
        if (guest.getProfileImage() != null) {
            dto.profileImage("/api/guests/" + guest.getId() + "/profile-image");
        }
    }

    public List<GuestResponseDto> toPagingGuest(List<Guests> guests) {
        return guests.stream().map(this::toDto).toList();
    }

    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "profileImage", ignore = true)
    public abstract Guests toEntity(CreateGuestRequestDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "profileImage", ignore = true)
    public abstract void updateEntity(UpdateGuestRequestDto dto, @MappingTarget Guests guest);
}
