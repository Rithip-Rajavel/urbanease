package com.urbanease.repository;

import com.urbanease.model.Message;
import com.urbanease.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findBySenderAndReceiverOrderByCreatedAtDesc(User sender, User receiver);
    
    List<Message> findByReceiverAndIsReadOrderByCreatedAtDesc(User receiver, boolean isRead);
    
    List<Message> findByBookingOrderByCreatedAtDesc(Long bookingId);
    
    @Query("SELECT m FROM Message m WHERE (m.sender = :user1 AND m.receiver = :user2) OR " +
           "(m.sender = :user2 AND m.receiver = :user1) ORDER BY m.createdAt ASC")
    List<Message> findConversationBetweenUsers(@Param("user1") User user1, @Param("user2") User user2);
    
    @Query("SELECT COUNT(m) FROM Message m WHERE m.receiver = :user AND m.isRead = false")
    Long countUnreadMessages(@Param("user") User user);
    
    @Query("SELECT m FROM Message m WHERE m.receiver = :user AND m.isRead = false " +
           "ORDER BY m.createdAt DESC")
    List<Message> findUnreadMessages(@Param("user") User user);
    
    @Query("SELECT m FROM Message m WHERE m.createdAt >= :since ORDER BY m.createdAt DESC")
    List<Message> findMessagesSince(@Param("since") LocalDateTime since);
}
