package com.room.management.service.impl;

import com.room.management.dto.request.CreateRoleRequestDto;
import com.room.management.dto.request.UpdateRoleRequestDto;
import com.room.management.dto.response.RoleResponseDto;
import com.room.management.entity.auth.Role;
import com.room.management.exception.DuplicateResourceException;
import com.room.management.exception.ResourceNotFoundException;
import com.room.management.mapper.RoleMapper;
import com.room.management.repository.RoleRepository;
import com.room.management.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    @Override
    @Transactional(readOnly = true)
    public List<RoleResponseDto> getAll() {
        return roleRepository.findAll().stream()
                .map(roleMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public RoleResponseDto getById(Long id) {
        return roleMapper.toDto(findRoleById(id));
    }

    @Override
    @Transactional
    public RoleResponseDto create(CreateRoleRequestDto request) {
        if (roleRepository.existsByRoleName(request.getRoleName())) {
            throw new DuplicateResourceException("Role", "roleName", request.getRoleName());
        }

        Role role = roleMapper.toEntity(request);
        role.setIsActive(true);

        Role saved = roleRepository.save(role);
        log.info("Role created: {}", saved.getRoleName());
        return roleMapper.toDto(saved);
    }

    @Override
    @Transactional
    public RoleResponseDto update(Long id, UpdateRoleRequestDto request) {
        Role role = findRoleById(id);

        if (StringUtils.hasText(request.getRoleName())
                && !request.getRoleName().equals(role.getRoleName())
                && roleRepository.existsByRoleName(request.getRoleName())) {
            throw new DuplicateResourceException("Role", "roleName", request.getRoleName());
        }

        roleMapper.updateEntity(request, role);

        Role saved = roleRepository.save(role);
        log.info("Role updated: {}", saved.getRoleName());
        return roleMapper.toDto(saved);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Role role = findRoleById(id);
        role.setIsActive(false);
        roleRepository.save(role);
        log.info("Role deactivated: {}", role.getRoleName());
    }

    private Role findRoleById(Long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role", id));
    }
}
