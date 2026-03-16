package com.urbanease.service;

import com.urbanease.dto.ServiceDto;
import com.urbanease.dto.ServiceCategoryDto;
import com.urbanease.model.Service;
import com.urbanease.model.ServiceCategory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ServiceMapper {

    public ServiceDto toServiceDto(com.urbanease.model.Service service) {
        if (service == null) return null;
        
        ServiceDto dto = new ServiceDto();
        dto.setId(service.getId());
        dto.setName(service.getName());
        dto.setDescription(service.getDescription());
        dto.setCategoryId(service.getCategory() != null ? service.getCategory().getId() : null);
        dto.setCategoryName(service.getCategory() != null ? service.getCategory().getName() : null);
        dto.setBasePrice(service.getBasePrice());
        dto.setEstimatedDuration(service.getEstimatedDuration());
        dto.setActive(service.isActive());
        dto.setCreatedAt(service.getCreatedAt());
        dto.setUpdatedAt(service.getUpdatedAt());
        return dto;
    }

    public ServiceCategoryDto toServiceCategoryDto(ServiceCategory category) {
        if (category == null) return null;
        
        ServiceCategoryDto dto = new ServiceCategoryDto();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        dto.setIconUrl(category.getIconUrl());
        dto.setActive(category.isActive());
        dto.setCreatedAt(category.getCreatedAt());
        dto.setUpdatedAt(category.getUpdatedAt());
        
        if (category.getServices() != null) {
            List<ServiceDto> serviceDtos = category.getServices().stream()
                    .map(this::toServiceDto)
                    .collect(Collectors.toList());
            dto.setServices(serviceDtos);
        }
        
        return dto;
    }

    public List<ServiceCategoryDto> toServiceCategoryDtoList(List<ServiceCategory> categories) {
        return categories.stream()
                .map(this::toServiceCategoryDto)
                .collect(Collectors.toList());
    }

    public List<ServiceDto> toServiceDtoList(List<com.urbanease.model.Service> services) {
        return services.stream()
                .map(this::toServiceDto)
                .collect(Collectors.toList());
    }
}
