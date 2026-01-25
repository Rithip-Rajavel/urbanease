package com.urbanease.controller;

import com.urbanease.model.ServiceCategory;
import com.urbanease.model.User;
import com.urbanease.repository.ServiceCategoryRepository;
import com.urbanease.service.LocationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
@Tag(name = "Services", description = "Service management APIs")
public class ServiceController {

    private final ServiceCategoryRepository serviceCategoryRepository;
    private final LocationService locationService;

    @GetMapping("/categories")
    @Operation(summary = "Get all active service categories")
    public ResponseEntity<List<ServiceCategory>> getActiveCategories() {
        List<ServiceCategory> categories = serviceCategoryRepository.findByIsActive(true);
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/providers/nearby")
    @Operation(summary = "Find nearby service providers")
    public ResponseEntity<?> findNearbyProviders(
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam(defaultValue = "10") double radius) {
        
        List<User> nearbyProviders = locationService.findNearbyProviders(latitude, longitude, radius);
        
        return ResponseEntity.ok(Map.of(
            "providers", nearbyProviders,
            "count", nearbyProviders.size(),
            "search_radius_km", radius
        ));
    }

    @PostMapping("/providers/location")
    @Operation(summary = "Update provider location")
    public ResponseEntity<?> updateProviderLocation(
            @AuthenticationPrincipal User currentUser,
            @RequestParam double latitude,
            @RequestParam double longitude) {
        
        if (!currentUser.getRole().equals(com.urbanease.model.UserRole.SERVICE_PROVIDER)) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Only service providers can update location"));
        }

        locationService.updateProviderLocation(currentUser.getId(), latitude, longitude);
        
        return ResponseEntity.ok(Map.of("message", "Location updated successfully"));
    }

    @GetMapping("/providers/distance")
    @Operation(summary = "Calculate distance between two points")
    public ResponseEntity<?> calculateDistance(
            @RequestParam double lat1,
            @RequestParam double lon1,
            @RequestParam double lat2,
            @RequestParam double lon2) {
        
        double distance = locationService.calculateDistance(lat1, lon1, lat2, lon2);
        
        return ResponseEntity.ok(Map.of(
            "distance_km", distance,
            "point1", Map.of("latitude", lat1, "longitude", lon1),
            "point2", Map.of("latitude", lat2, "longitude", lon2)
        ));
    }
}
