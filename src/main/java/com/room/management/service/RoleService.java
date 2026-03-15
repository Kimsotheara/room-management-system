package com.room.management.service;

import com.room.management.dto.request.CreateRoleRequestDto;
import com.room.management.dto.request.UpdateRoleRequestDto;
import com.room.management.dto.response.RoleResponseDto;

import java.util.List;

public interface RoleService {

    List<RoleResponseDto> getAll();

    RoleResponseDto getById(Long id);

    RoleResponseDto create(CreateRoleRequestDto request);

    RoleResponseDto update(Long id, UpdateRoleRequestDto request);

    void delete(Long id);
}
