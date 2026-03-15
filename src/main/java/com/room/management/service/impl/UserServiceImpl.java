package com.room.management.service.impl;

import com.room.management.dto.request.*;
import com.room.management.dto.response.PageAbleResponse;
import com.room.management.dto.response.UserResponseDto;
import com.room.management.entity.auth.Role;
import com.room.management.entity.auth.User;
import com.room.management.entity.auth.UserRole;
import com.room.management.exception.DuplicateResourceException;
import com.room.management.exception.ResourceNotFoundException;
import com.room.management.mapper.UserMapper;
import com.room.management.repository.RoleRepository;
import com.room.management.repository.UserRepository;
import com.room.management.repository.UserRoleRepository;
import com.room.management.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    @Transactional(readOnly = true)
    public PageAbleResponse<User, UserResponseDto, Void> getUsersWithFilter(PageAbleRequest<UserRequestDto> request) {
        log.info("Fetching users with filter and pagination");
        Page<User> page = userRepository.findAllWithFilter(request);
        return PageAbleResponse.withoutAddition(page, userMapper.toPagingUser(page.getContent()));
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getById(Long id) {
        return userMapper.toDto(findUserById(id));
    }

    @Override
    @Transactional
    public UserResponseDto create(CreateUserRequestDto request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("User", "username", request.getUsername());
        }
        if (StringUtils.hasText(request.getEmail()) && userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("User", "email", request.getEmail());
        }

        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setIsActive(true);

        User saved = userRepository.save(user);
        log.info("User created: {}", saved.getUsername());
        return userMapper.toDto(saved);
    }

    @Override
    @Transactional
    public UserResponseDto update(Long id, UpdateUserRequestDto request) {
        User user = findUserById(id);

        if (StringUtils.hasText(request.getEmail())
                && !request.getEmail().equals(user.getEmail())
                && userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("User", "email", request.getEmail());
        }

        userMapper.updateEntity(request, user);

        User saved = userRepository.save(user);
        log.info("User updated: {}", saved.getUsername());
        return userMapper.toDto(saved);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        User user = findUserById(id);
        user.setIsActive(false);
        userRepository.save(user);
        log.info("User deactivated: {}", user.getUsername());
    }

    @Override
    @Transactional
    public UserResponseDto assignRole(Long userId, AssignRoleRequestDto request) {
        User user = findUserById(userId);
        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("Role", request.getRoleId()));

        if (!Boolean.TRUE.equals(role.getIsActive())) {
            throw new IllegalArgumentException("Role is inactive: " + role.getRoleName());
        }
        if (userRoleRepository.existsActiveByUserIdAndRoleId(userId, role.getRoleId())) {
            throw new DuplicateResourceException("User already has role: " + role.getRoleName());
        }

        UserRole userRole = new UserRole();
        userRole.setUser(user);
        userRole.setRole(role);
        userRole.setIsActive(true);
        userRole.setExpiresAt(request.getExpiresAt());
        userRoleRepository.save(userRole);

        log.info("Role '{}' assigned to user '{}'", role.getRoleName(), user.getUsername());
        return userMapper.toDto(findUserById(userId));
    }

    @Override
    @Transactional
    public UserResponseDto removeRole(Long userId, Long roleId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", userId);
        }
        if (!roleRepository.existsById(roleId)) {
            throw new ResourceNotFoundException("Role", roleId);
        }
        if (!userRoleRepository.existsActiveByUserIdAndRoleId(userId, roleId)) {
            throw new IllegalArgumentException("User does not have the specified role");
        }

        userRoleRepository.deactivateByUserIdAndRoleId(userId, roleId);
        log.info("Role {} removed from user {}", roleId, userId);
        return userMapper.toDto(findUserById(userId));
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
    }
}
