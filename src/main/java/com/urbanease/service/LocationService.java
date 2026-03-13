package com.urbanease.service;

import com.urbanease.model.User;
import com.urbanease.model.UserRole;
import com.urbanease.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LocationService {

    private final UserRepository userRepository;

    public void updateProviderLocation(Long providerId, double latitude, double longitude) {
        User provider = userRepository.findById(providerId)
                .orElseThrow(() -> new RuntimeException("Provider not found"));

        if (!provider.getRole().equals(UserRole.SERVICE_PROVIDER)) {
            throw new RuntimeException("User is not a service provider");
        }

        // Note: currentLocation has been removed from User entity.
        // Location updates should be stored in a dedicated table (e.g., ProviderProfile).
        log.info("Location update requested for provider {} ({}, {}) — not persisted (no location column on User)", providerId, latitude, longitude);
    }

    public List<User> findNearbyProviders(double customerLatitude, double customerLongitude, double searchRadiusKm) {
        // Returns all available service providers — distance filtering handled client-side or via ProviderProfile
        return userRepository.findByRoleAndIsAvailable(UserRole.SERVICE_PROVIDER, true);
    }

    public double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radius of the earth in km

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c;

        return distance;
    }

    public boolean isProviderInServiceArea(double providerLat, double providerLon, 
                                          double customerLat, double customerLon, 
                                          double maxDistanceKm) {
        double distance = calculateDistance(providerLat, providerLon, customerLat, customerLon);
        return distance <= maxDistanceKm;
    }
}
