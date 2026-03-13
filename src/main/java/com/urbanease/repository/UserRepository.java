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

    // Nearby providers: use ProviderProfile-based search (User no longer stores coordinates)
    default java.util.List<User> findNearbyProviders(UserRole role, double latitude, double longitude, double distance) {
        return findByRoleAndIsAvailable(role, true);
    }

    @Query("SELECT u FROM User u WHERE u.otpCode = :otpCode AND u.otpExpiry > CURRENT_TIMESTAMP")
    Optional<User> findByValidOtp(@Param("otpCode") String otpCode);
}
