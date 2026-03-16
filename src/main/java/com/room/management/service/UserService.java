package com.room.management.service;

import com.room.management.dto.request.AssignRoleRequestDto;
import com.room.management.dto.request.CreateUserRequestDto;
import com.room.management.dto.request.UpdateUserRequestDto;
import com.room.management.dto.request.UserRequestDto;
import com.room.management.dto.request.PageAbleRequest;
import com.room.management.dto.response.PageAbleResponse;
import com.room.management.dto.response.UserResponseDto;
import com.room.management.entity.auth.User;

public interface UserService {

    PageAbleResponse<User,UserResponseDto,Void> getUsersWithFilter(PageAbleRequest<UserRequestDto> request);

    UserResponseDto getById(Long id);

    UserResponseDto create(CreateUserRequestDto request);

    UserResponseDto update(Long id, UpdateUserRequestDto request);

    void delete(Long id);

    UserResponseDto assignRole(Long userId, AssignRoleRequestDto request);

    UserResponseDto removeRole(Long userId, Long roleId);
}
