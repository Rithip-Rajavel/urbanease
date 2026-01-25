package com.urbanease.repository;

import com.urbanease.model.Booking;
import com.urbanease.model.BookingStatus;
import com.urbanease.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByCustomer(User customer);
    
    List<Booking> findByProvider(User provider);
    
    List<Booking> findByStatus(BookingStatus status);
    
    List<Booking> findByCustomerAndStatus(User customer, BookingStatus status);
    
    List<Booking> findByProviderAndStatus(User provider, BookingStatus status);
    
    @Query("SELECT b FROM Booking b WHERE b.status = :status AND b.scheduledTime BETWEEN :startTime AND :endTime")
    List<Booking> findByStatusAndScheduledTimeBetween(@Param("status") BookingStatus status,
                                                      @Param("startTime") LocalDateTime startTime,
                                                      @Param("endTime") LocalDateTime endTime);
    
    @Query("SELECT b FROM Booking b WHERE b.provider.id = :providerId AND b.status IN :statuses")
    List<Booking> findByProviderIdAndStatusIn(@Param("providerId") Long providerId,
                                               @Param("statuses") List<BookingStatus> statuses);
    
    Optional<Booking> findByIdAndCustomer(Long id, User customer);
    
    Optional<Booking> findByIdAndProvider(Long id, User provider);
}
