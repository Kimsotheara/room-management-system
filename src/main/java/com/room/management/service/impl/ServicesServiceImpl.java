package com.room.management.service.impl;


import com.room.management.dto.request.CreateServiceRequestDto;
import com.room.management.dto.request.PageAbleRequest;
import com.room.management.dto.request.UpdateServiceRequestDto;
import com.room.management.dto.response.PageAbleResponse;
import com.room.management.dto.response.ServiceResponseDto;
import com.room.management.entity.auth.User;
import com.room.management.entity.room.Services;
import com.room.management.exception.DuplicateResourceException;
import com.room.management.exception.ResourceNotFoundException;
import com.room.management.mapper.ServicesMapper;
import com.room.management.repository.ServicesRepository;
import com.room.management.service.ServicesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServicesServiceImpl implements ServicesService {

    private final ServicesRepository servicesRepository;
    private final ServicesMapper servicesMapper;


    @Override
    public List<ServiceResponseDto> getAllServices() {
        return servicesRepository.findAll().stream()
                .map(servicesMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceResponseDto getById(Long id) {
        return servicesMapper.toDto(findById(id));
    }

    @Override
    @Transactional
    public ServiceResponseDto create(CreateServiceRequestDto request) {

        if(servicesRepository.existsByName(request.getName())){
            throw new DuplicateResourceException("Service", "name", request.getName());
        }

        Services obj = servicesRepository.save(servicesMapper.toEntity(request));
        log.info("Service created: {}", obj.getName());

        return servicesMapper.toDto(obj);
    }

    @Override
    @Transactional
    public ServiceResponseDto update(Long id, UpdateServiceRequestDto request) {

        Services obj = findById(id);

        if (StringUtils.hasText(request.getName())
                && !request.getName().equals(obj.getName())
                && servicesRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Service", "name", request.getName());
        }

        servicesMapper.updateEntity(request, obj);
        Services updatedObj = servicesRepository.save(obj);
        log.info("Service updated: {}", updatedObj.getName());
        return servicesMapper.toDto(updatedObj);
    }

    @Override
    @Transactional
    public void delete(Long id) {

        Services service = findById(id);
        service.setIsActive(false);
        servicesRepository.save(service);
        log.info("Service deactivated: {}", service.getName());

    }

    private Services findById(Long id) {
        return servicesRepository.findById(id)
                 .orElseThrow(() -> new ResourceNotFoundException("Service", id));
    }
}
