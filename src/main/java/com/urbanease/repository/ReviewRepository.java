package com.urbanease.repository;

import com.urbanease.model.Booking;
import com.urbanease.model.ProviderProfile;
import com.urbanease.model.Review;
import com.urbanease.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByProvider(ProviderProfile provider);
    
    List<Review> findByCustomer(User customer);
    
    Optional<Review> findByBooking(Booking booking);
    
    boolean existsByBooking(Booking booking);
    
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.provider = :provider")
    Double calculateAverageRating(@Param("provider") ProviderProfile provider);
    
    @Query("SELECT COUNT(r) FROM Review r WHERE r.provider = :provider")
    Long countReviews(@Param("provider") ProviderProfile provider);
    
    @Query("SELECT r FROM Review r WHERE r.provider = :provider ORDER BY r.createdAt DESC")
    List<Review> findByProviderOrderByCreatedAtDesc(@Param("provider") ProviderProfile provider);
    
    @Query("SELECT r FROM Review r WHERE r.rating >= :minRating AND r.provider = :provider")
    List<Review> findByProviderAndMinRating(@Param("provider") ProviderProfile provider, 
                                           @Param("minRating") Integer minRating);
}
