package com.room.management.service;

import com.room.management.dto.request.AssignRoleRequestDto;
import com.room.management.dto.request.CreateUserRequestDto;
import com.room.management.dto.request.UpdateUserRequestDto;
import com.room.management.dto.response.UserResponseDto;

import java.util.List;

public interface UserService {

    List<UserResponseDto> getAll();

    UserResponseDto getById(Long id);

    UserResponseDto create(CreateUserRequestDto request);

    UserResponseDto update(Long id, UpdateUserRequestDto request);

    void delete(Long id);

    UserResponseDto assignRole(Long userId, AssignRoleRequestDto request);

    UserResponseDto removeRole(Long userId, Long roleId);
}
