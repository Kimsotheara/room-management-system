package com.room.management.mapper;

import com.room.management.dto.request.CreateUserRequestDto;
import com.room.management.dto.request.UpdateUserRequestDto;
import com.room.management.dto.response.RoleResponseDto;
import com.room.management.dto.response.UserResponseDto;
import com.room.management.entity.auth.User;
import com.room.management.entity.auth.UserRole;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring",
        unmappedSourcePolicy = ReportingPolicy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class UserMapper {

    @Autowired
    protected RoleMapper roleMapper;

    // ── Entity → Response DTO ──────────────────────────────────────────────

    @Mapping(target = "roles", ignore = true)
    public abstract UserResponseDto toDto(User user);

    @AfterMapping
    protected void mapActiveRoles(User user, @MappingTarget UserResponseDto.UserResponseDtoBuilder dto) {
        if (user.getUserRoles() == null) return;
        Set<RoleResponseDto> roles = user.getUserRoles().stream()
                .filter(ur -> Boolean.TRUE.equals(ur.getIsActive()))
                .map(UserRole::getRole)
                .map(roleMapper::toDto)
                .collect(Collectors.toSet());
        dto.roles(roles);
    }

    public List<UserResponseDto> toPagingUser(List<User> users) {
        return users.stream().map(this::toDto).collect(Collectors.toList());
    }

    // ── Request DTO → Entity ───────────────────────────────────────────────

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    public abstract User toEntity(CreateUserRequestDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void updateEntity(UpdateUserRequestDto dto, @MappingTarget User user);
}
