package com.urbanease.controller;

import com.urbanease.dto.UserProfileDto;
import com.urbanease.model.User;
import com.urbanease.model.UserRole;
import com.urbanease.repository.ProviderProfileRepository;
import com.urbanease.service.UserProfileMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
@Tag(name = "Profile", description = "User profile management APIs")
public class ProfileController {

    private final ProviderProfileRepository providerProfileRepository;
    private final UserProfileMapper userProfileMapper;

    @GetMapping("/me")
    @Operation(summary = "Get current user profile")
    public ResponseEntity<UserProfileDto> getCurrentUserProfile(@AuthenticationPrincipal User currentUser) {
        UserProfileDto profileDto;
        
        if (currentUser.getRole() == UserRole.SERVICE_PROVIDER) {
            Optional<com.urbanease.model.ProviderProfile> providerProfile = 
                providerProfileRepository.findByUserId(currentUser.getId());
            
            if (providerProfile.isPresent()) {
                profileDto = userProfileMapper.toUserProfileDto(currentUser, providerProfile.get());
            } else {
                profileDto = userProfileMapper.toUserProfileDto(currentUser);
            }
        } else {
            profileDto = userProfileMapper.toUserProfileDto(currentUser);
        }
        
        return ResponseEntity.ok(profileDto);
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Get user profile by ID")
    public ResponseEntity<?> getUserProfile(@PathVariable Long userId) {
        // This would typically require admin privileges or public profile access
        // For now, we'll implement a basic version
        
        Optional<com.urbanease.model.ProviderProfile> providerProfile = 
            providerProfileRepository.findByUserId(userId);
        
        if (providerProfile.isPresent()) {
            UserProfileDto profileDto = userProfileMapper.toUserProfileDto(
                providerProfile.get().getUser(), providerProfile.get());
            return ResponseEntity.ok(profileDto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/providers/{providerId}")
    @Operation(summary = "Get provider profile by provider ID")
    public ResponseEntity<?> getProviderProfile(@PathVariable Long providerId) {
        Optional<com.urbanease.model.ProviderProfile> providerProfile = 
            providerProfileRepository.findById(providerId);
        
        if (providerProfile.isPresent()) {
            UserProfileDto profileDto = userProfileMapper.toUserProfileDto(
                providerProfile.get().getUser(), providerProfile.get());
            return ResponseEntity.ok(profileDto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
