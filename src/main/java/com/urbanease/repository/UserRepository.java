package com.urbanease.repository;

import com.urbanease.model.User;
import com.urbanease.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    Optional<User> findByMobileNumber(String mobileNumber);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
    
    boolean existsByMobileNumber(String mobileNumber);
    
    List<User> findByRole(UserRole role);
    
    List<User> findByRoleAndIsAvailable(UserRole role, boolean isAvailable);
    
    @Query("SELECT u FROM User u WHERE u.role = :role AND u.isAvailable = true AND " +
           "(6371 * acos(cos(radians(:latitude)) * cos(radians(u.currentLocation.latitude)) * " +
           "cos(radians(u.currentLocation.longitude) - radians(:longitude)) + " +
           "sin(radians(:latitude)) * sin(radians(u.currentLocation.latitude)))) < :distance")
    List<User> findNearbyProviders(@Param("role") UserRole role, 
                                  @Param("latitude") double latitude,
                                  @Param("longitude") double longitude,
                                  @Param("distance") double distance);
    
    @Query("SELECT u FROM User u WHERE u.otpCode = :otpCode AND u.otpExpiry > CURRENT_TIMESTAMP")
    Optional<User> findByValidOtp(@Param("otpCode") String otpCode);
}
