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
    
    @Query(value = "SELECT pp.* FROM provider_profiles pp " +
                   "JOIN users u ON pp.user_id = u.id " +
                   "WHERE u.is_available = true " +
                   "AND (6371 * acos(cos(radians(:latitude)) * cos(radians(u.latitude)) * " +
                   "cos(radians(u.longitude) - radians(:longitude)) + " +
                   "sin(radians(:latitude)) * sin(radians(u.latitude)))) < :distance",
           nativeQuery = true)
    List<ProviderProfile> findNearbyProviders(@Param("latitude") double latitude,
                                             @Param("longitude") double longitude,
                                             @Param("distance") double distance);
    
    @Query("SELECT p FROM ProviderProfile p WHERE p.averageRating >= :minRating AND p.user.isAvailable = true")
    List<ProviderProfile> findByMinRating(@Param("minRating") double minRating);
}
