package com.room.management.mapper;

import com.room.management.dto.response.ServiceUsageResponseDto;
import com.room.management.entity.room.ServiceUsages;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ServiceUsageMapper {

    @Mapping(source = "reservation.id", target = "reservationId")
    @Mapping(source = "service.id", target = "serviceId")
    @Mapping(source = "service.name", target = "serviceName")
    ServiceUsageResponseDto toDto(ServiceUsages serviceUsage);

    default List<ServiceUsageResponseDto> toDtoList(List<ServiceUsages> list) {
        return list.stream().map(this::toDto).toList();
    }
}
