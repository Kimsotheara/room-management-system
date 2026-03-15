package com.room.management.mapper;

import com.room.management.dto.request.CreateRoleRequestDto;
import com.room.management.dto.request.UpdateRoleRequestDto;
import com.room.management.dto.response.RoleResponseDto;
import com.room.management.entity.auth.Role;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RoleMapper {

    // ── Entity → Response DTO ──────────────────────────────────────────────

    RoleResponseDto toDto(Role role);

    // ── Request DTO → Entity ───────────────────────────────────────────────

    @Mapping(target = "isActive", ignore = true)
    Role toEntity(CreateRoleRequestDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(UpdateRoleRequestDto dto, @MappingTarget Role role);
}
