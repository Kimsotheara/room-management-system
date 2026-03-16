package com.room.management.mapper;

import com.room.management.dto.request.CreateServiceRequestDto;
import com.room.management.dto.request.UpdateServiceRequestDto;
import com.room.management.dto.response.ServiceResponseDto;
import com.room.management.entity.room.Services;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ServicesMapper {

    ServiceResponseDto toDto(Services services);

    @Mapping(target = "isActive", ignore = true)
    Services toEntity(CreateServiceRequestDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(UpdateServiceRequestDto dto, @MappingTarget Services services);

    public default List<ServiceResponseDto> toPagingService(List<Services> services) {
        return services.stream().map(this::toDto).toList();
    }
}
