package com.urbanease.service;

import com.urbanease.dto.ReviewRequest;
import com.urbanease.model.Booking;
import com.urbanease.model.BookingStatus;
import com.urbanease.model.ProviderProfile;
import com.urbanease.model.Review;
import com.urbanease.model.User;
import com.urbanease.repository.BookingRepository;
import com.urbanease.repository.ProviderProfileRepository;
import com.urbanease.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookingRepository bookingRepository;
    private final ProviderProfileRepository providerProfileRepository;

    @Transactional
    public Review createReview(ReviewRequest request, User customer) {
        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (!booking.getCustomer().getId().equals(customer.getId())) {
            throw new RuntimeException("You can only review your own bookings");
        }

        if (!booking.getStatus().equals(BookingStatus.COMPLETED)) {
            throw new RuntimeException("You can only review completed bookings");
        }

        if (reviewRepository.existsByBooking(booking)) {
            throw new RuntimeException("Review already exists for this booking");
        }

        ProviderProfile providerProfile = providerProfileRepository.findByUserId(booking.getProvider().getId())
                .orElseThrow(() -> new RuntimeException("Provider profile not found"));

        Review review = new Review();
        review.setCustomer(customer);
        review.setProvider(providerProfile);
        review.setBooking(booking);
        review.setRating(request.getRating());
        review.setComment(request.getComment());

        Review savedReview = reviewRepository.save(review);

        updateProviderRating(providerProfile);

        log.info("Created review {} for booking {} by customer {}", 
                savedReview.getId(), booking.getId(), customer.getId());

        return savedReview;
    }

    @Transactional
    public List<Review> getProviderReviews(Long providerId) {
        ProviderProfile providerProfile = providerProfileRepository.findByUserId(providerId)
                .orElseThrow(() -> new RuntimeException("Provider profile not found"));

        return reviewRepository.findByProviderOrderByCreatedAtDesc(providerProfile);
    }

    @Transactional
    public List<Review> getCustomerReviews(User customer) {
        return reviewRepository.findByCustomer(customer);
    }

    @Transactional
    public Review getBookingReview(Long bookingId, User user) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (!booking.getCustomer().getId().equals(user.getId()) && 
            !booking.getProvider().getId().equals(user.getId())) {
            throw new RuntimeException("You are not authorized to view this review");
        }

        return reviewRepository.findByBooking(booking)
                .orElseThrow(() -> new RuntimeException("Review not found for this booking"));
    }

    @Transactional
    public void updateProviderRating(ProviderProfile providerProfile) {
        Double averageRating = reviewRepository.calculateAverageRating(providerProfile);
        Long totalReviews = reviewRepository.countReviews(providerProfile);

        providerProfile.setAverageRating(averageRating != null ? averageRating : 0.0);
        providerProfile.setTotalReviews(totalReviews.intValue());

        providerProfileRepository.save(providerProfile);

        log.info("Updated rating for provider {}: {} ({} reviews)", 
                providerProfile.getId(), averageRating, totalReviews);
    }

    @Transactional
    public void deleteReview(Long reviewId, User customer) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        if (!review.getCustomer().getId().equals(customer.getId())) {
            throw new RuntimeException("You can only delete your own reviews");
        }

        ProviderProfile providerProfile = review.getProvider();
        reviewRepository.delete(review);

        updateProviderRating(providerProfile);

        log.info("Deleted review {} by customer {}", reviewId, customer.getId());
    }

    @Transactional
    public Double getProviderAverageRating(Long providerId) {
        ProviderProfile providerProfile = providerProfileRepository.findByUserId(providerId)
                .orElseThrow(() -> new RuntimeException("Provider profile not found"));

        return reviewRepository.calculateAverageRating(providerProfile);
    }

    @Transactional
    public Long getProviderReviewCount(Long providerId) {
        ProviderProfile providerProfile = providerProfileRepository.findByUserId(providerId)
                .orElseThrow(() -> new RuntimeException("Provider profile not found"));

        return reviewRepository.countReviews(providerProfile);
    }
}
