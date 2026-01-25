package com.urbanease.controller;

import com.urbanease.dto.ReviewRequest;
import com.urbanease.model.Review;
import com.urbanease.model.User;
import com.urbanease.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Tag(name = "Reviews", description = "Review and rating management APIs")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    @Operation(summary = "Create a new review")
    public ResponseEntity<Review> createReview(
            @Valid @RequestBody ReviewRequest request,
            @AuthenticationPrincipal User customer) {
        
        Review review = reviewService.createReview(request, customer);
        return ResponseEntity.ok(review);
    }

    @GetMapping("/provider/{providerId}")
    @Operation(summary = "Get all reviews for a provider")
    public ResponseEntity<List<Review>> getProviderReviews(@PathVariable Long providerId) {
        List<Review> reviews = reviewService.getProviderReviews(providerId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/my-reviews")
    @Operation(summary = "Get current user's reviews")
    public ResponseEntity<List<Review>> getMyReviews(@AuthenticationPrincipal User customer) {
        List<Review> reviews = reviewService.getCustomerReviews(customer);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/booking/{bookingId}")
    @Operation(summary = "Get review for a specific booking")
    public ResponseEntity<Review> getBookingReview(
            @PathVariable Long bookingId,
            @AuthenticationPrincipal User user) {
        
        Review review = reviewService.getBookingReview(bookingId, user);
        return ResponseEntity.ok(review);
    }

    @GetMapping("/provider/{providerId}/stats")
    @Operation(summary = "Get provider rating statistics")
    public ResponseEntity<Map<String, Object>> getProviderStats(@PathVariable Long providerId) {
        Double averageRating = reviewService.getProviderAverageRating(providerId);
        Long totalReviews = reviewService.getProviderReviewCount(providerId);

        Map<String, Object> stats = new HashMap<>();
        stats.put("averageRating", averageRating != null ? averageRating : 0.0);
        stats.put("totalReviews", totalReviews != null ? totalReviews : 0);

        return ResponseEntity.ok(stats);
    }

    @DeleteMapping("/{reviewId}")
    @Operation(summary = "Delete a review")
    public ResponseEntity<Map<String, String>> deleteReview(
            @PathVariable Long reviewId,
            @AuthenticationPrincipal User customer) {
        
        reviewService.deleteReview(reviewId, customer);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Review deleted successfully");
        return ResponseEntity.ok(response);
    }
}
