package com.urbanease.service;

import com.urbanease.dto.UserProfileDto;
import com.urbanease.model.ProviderProfile;
import com.urbanease.model.User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserProfileMapper {

    public UserProfileDto toUserProfileDto(User user) {
        if (user == null) return null;
        
        UserProfileDto dto = new UserProfileDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setMobileNumber(user.getMobileNumber());
        dto.setRole(user.getRole());
        dto.setActive(user.isActive());
        dto.setAvailable(user.isAvailable());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        dto.setLastLogin(user.getLastLogin());
        
        return dto;
    }

    public UserProfileDto toUserProfileDto(User user, ProviderProfile providerProfile) {
        if (user == null) return null;
        
        UserProfileDto dto = toUserProfileDto(user);
        
        if (providerProfile != null) {
            dto.setFirstName(providerProfile.getFirstName());
            dto.setLastName(providerProfile.getLastName());
            dto.setFullName(providerProfile.getFullName());
            dto.setBio(providerProfile.getBio());
            dto.setProfileImageUrl(providerProfile.getProfileImageUrl());
            dto.setYearsOfExperience(providerProfile.getYearsOfExperience());
            dto.setAverageRating(providerProfile.getAverageRating());
            dto.setTotalReviews(providerProfile.getTotalReviews());
            dto.setCompletedJobs(providerProfile.getCompletedJobs());
            dto.setVerificationStatus(providerProfile.getVerificationStatus());
            dto.setBusinessName(providerProfile.getBusinessName());
            dto.setBusinessLicense(providerProfile.getBusinessLicense());
        }
        
        return dto;
    }
}
