package com.urbanease.controller;

import com.urbanease.dto.RoleChangeRequest;
import com.urbanease.model.*;
import com.urbanease.repository.*;
import com.urbanease.service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin", description = "Admin management APIs")
public class AdminController {

    private final UserRepository userRepository;
    private final ServiceCategoryRepository serviceCategoryRepository;
    private final BookingRepository bookingRepository;
    private final ReviewRepository reviewRepository;
    private final ProviderProfileRepository providerProfileRepository;
    private final EmailService emailService;

    @GetMapping("/dashboard")
    @Operation(summary = "Get admin dashboard statistics")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        long totalCustomers = userRepository.findByRole(UserRole.CUSTOMER).size();
        long totalProviders = userRepository.findByRole(UserRole.SERVICE_PROVIDER).size();
        long totalBookings = bookingRepository.count();
        long totalReviews = reviewRepository.count();
        long pendingVerifications = providerProfileRepository.findByVerificationStatus(VerificationStatus.PENDING).size();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalCustomers", totalCustomers);
        stats.put("totalProviders", totalProviders);
        stats.put("totalBookings", totalBookings);
        stats.put("totalReviews", totalReviews);
        stats.put("pendingVerifications", pendingVerifications);

        return ResponseEntity.ok(stats);
    }

    @GetMapping("/users")
    @Operation(summary = "Get all users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }

    @PostMapping("/users/{userId}/deactivate")
    @Operation(summary = "Deactivate a user")
    public ResponseEntity<Map<String, String>> deactivateUser(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setActive(false);
        userRepository.save(user);

        Map<String, String> response = new HashMap<>();
        response.put("message", "User deactivated successfully");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/users/{userId}/activate")
    @Operation(summary = "Activate a user")
    public ResponseEntity<Map<String, String>> activateUser(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setActive(true);
        userRepository.save(user);

        Map<String, String> response = new HashMap<>();
        response.put("message", "User activated successfully");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/users/change-role")
    @Operation(summary = "Change user role")
    public ResponseEntity<Map<String, Object>> changeUserRole(@Valid @RequestBody RoleChangeRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserRole oldRole = user.getRole();
        user.setRole(request.getNewRole());
        userRepository.save(user);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "User role changed successfully");
        response.put("userId", user.getId());
        response.put("username", user.getUsername());
        response.put("oldRole", oldRole);
        response.put("newRole", user.getRole());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/providers/pending-verification")
    @Operation(summary = "Get providers pending verification")
    public ResponseEntity<List<ProviderProfile>> getPendingVerifications() {
        List<ProviderProfile> providers = providerProfileRepository.findByVerificationStatus(VerificationStatus.PENDING);
        return ResponseEntity.ok(providers);
    }

    @PostMapping("/providers/{providerId}/verify")
    @Operation(summary = "Verify a provider")
    public ResponseEntity<Map<String, String>> verifyProvider(@PathVariable Long providerId) {
        ProviderProfile provider = providerProfileRepository.findById(providerId)
                .orElseThrow(() -> new RuntimeException("Provider not found"));

        provider.setVerificationStatus(VerificationStatus.VERIFIED);
        providerProfileRepository.save(provider);

        if (provider.getUser().getEmail() != null) {
            emailService.sendPasswordResetConfirmation(provider.getUser().getEmail());
        }

        Map<String, String> response = new HashMap<>();
        response.put("message", "Provider verified successfully");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/providers/{providerId}/reject")
    @Operation(summary = "Reject provider verification")
    public ResponseEntity<Map<String, String>> rejectProvider(@PathVariable Long providerId) {
        ProviderProfile provider = providerProfileRepository.findById(providerId)
                .orElseThrow(() -> new RuntimeException("Provider not found"));

        provider.setVerificationStatus(VerificationStatus.REJECTED);
        providerProfileRepository.save(provider);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Provider verification rejected");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/categories")
    @Operation(summary = "Create a new service category")
    public ResponseEntity<ServiceCategory> createCategory(@RequestBody ServiceCategory category) {
        ServiceCategory savedCategory = serviceCategoryRepository.save(category);
        return ResponseEntity.ok(savedCategory);
    }

    @PutMapping("/categories/{categoryId}")
    @Operation(summary = "Update a service category")
    public ResponseEntity<ServiceCategory> updateCategory(
            @PathVariable Long categoryId,
            @RequestBody ServiceCategory category) {
        
        ServiceCategory existingCategory = serviceCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        existingCategory.setName(category.getName());
        existingCategory.setDescription(category.getDescription());
        existingCategory.setIconUrl(category.getIconUrl());
        existingCategory.setActive(category.isActive());

        ServiceCategory updatedCategory = serviceCategoryRepository.save(existingCategory);
        return ResponseEntity.ok(updatedCategory);
    }

    @GetMapping("/bookings")
    @Operation(summary = "Get all bookings")
    public ResponseEntity<List<Booking>> getAllBookings() {
        List<Booking> bookings = bookingRepository.findAll();
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/reviews")
    @Operation(summary = "Get all reviews")
    public ResponseEntity<List<Review>> getAllReviews() {
        List<Review> reviews = reviewRepository.findAll();
        return ResponseEntity.ok(reviews);
    }
}
