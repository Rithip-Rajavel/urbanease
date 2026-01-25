package com.urbanease.repository;

import com.urbanease.model.ProviderProfile;
import com.urbanease.model.VerificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProviderProfileRepository extends JpaRepository<ProviderProfile, Long> {

    Optional<ProviderProfile> findByUserId(Long userId);
    
    List<ProviderProfile> findByVerificationStatus(VerificationStatus status);
    
    @Query("SELECT p FROM ProviderProfile p WHERE p.user.isAvailable = true AND " +
           "(6371 * acos(cos(radians(:latitude)) * cos(radians(p.user.currentLocation.latitude)) * " +
           "cos(radians(p.user.currentLocation.longitude) - radians(:longitude)) + " +
           "sin(radians(:latitude)) * sin(radians(p.user.currentLocation.latitude)))) < :distance")
    List<ProviderProfile> findNearbyProviders(@Param("latitude") double latitude,
                                             @Param("longitude") double longitude,
                                             @Param("distance") double distance);
    
    @Query("SELECT p FROM ProviderProfile p WHERE p.averageRating >= :minRating AND p.user.isAvailable = true")
    List<ProviderProfile> findByMinRating(@Param("minRating") double minRating);
}
