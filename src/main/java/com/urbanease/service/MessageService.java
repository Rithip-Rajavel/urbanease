package com.urbanease.service;

import com.urbanease.dto.ConversationDto;
import com.urbanease.dto.MessageRequest;
import com.urbanease.model.Booking;
import com.urbanease.model.Message;
import com.urbanease.model.User;
import com.urbanease.repository.BookingRepository;
import com.urbanease.repository.MessageRepository;
import com.urbanease.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final OneSignalService oneSignalService;
    private final ConversationMapper conversationMapper;

    @Transactional
    public Message sendMessage(MessageRequest request, User sender) {
        User receiver = userRepository.findById(request.getReceiverId())
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        if (sender.getId().equals(receiver.getId())) {
            throw new RuntimeException("Cannot send message to yourself");
        }

        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(request.getContent());

        if (request.getBookingId() != null) {
            Booking booking = bookingRepository.findById(request.getBookingId())
                    .orElseThrow(() -> new RuntimeException("Booking not found"));
            
            if (!booking.getCustomer().getId().equals(sender.getId()) && 
                !booking.getProvider().getId().equals(sender.getId())) {
                throw new RuntimeException("You are not part of this booking");
            }
            
            message.setBooking(booking);
        }

        Message savedMessage = messageRepository.save(message);

        // Send push notification to receiver
        try {
            oneSignalService.sendMessageNotification(
                receiver.getId(), 
                sender.getUsername(), 
                savedMessage.getContent()
            );
        } catch (Exception e) {
            log.warn("Failed to send push notification: {}", e.getMessage());
        }

        log.info("Message sent from {} to {}: {}", sender.getId(), receiver.getId(), savedMessage.getId());
        return savedMessage;
    }

    @Transactional
    public List<Message> getConversation(Long otherUserId, User currentUser) {
        User otherUser = userRepository.findById(otherUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return messageRepository.findConversationBetweenUsers(currentUser, otherUser);
    }

    @Transactional
    public List<Message> getBookingMessages(Long bookingId, User currentUser) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (!booking.getCustomer().getId().equals(currentUser.getId()) && 
            !booking.getProvider().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You are not part of this booking");
        }

        return messageRepository.findByBookingIdOrderByCreatedAtDesc(bookingId);
    }

    @Transactional
    public List<Message> getUnreadMessages(User user) {
        return messageRepository.findUnreadMessages(user);
    }

    @Transactional
    public Long getUnreadMessageCount(User user) {
        return messageRepository.countUnreadMessages(user);
    }

    @Transactional
    public void markMessagesAsRead(Long senderId, User currentUser) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found"));

        List<Message> unreadMessages = messageRepository.findByReceiverAndIsReadOrderByCreatedAtDesc(currentUser, false);
        
        unreadMessages.stream()
                .filter(msg -> msg.getSender().getId().equals(senderId))
                .forEach(msg -> {
                    msg.setRead(true);
                    messageRepository.save(msg);
                });

        log.info("Marked {} messages as read from {} to {}", 
                unreadMessages.size(), senderId, currentUser.getId());
    }

    @Transactional
    public void markAllMessagesAsRead(User currentUser) {
        List<Message> unreadMessages = messageRepository.findUnreadMessages(currentUser);
        
        unreadMessages.forEach(msg -> {
            msg.setRead(true);
            messageRepository.save(msg);
        });

        log.info("Marked {} messages as read for user {}", unreadMessages.size(), currentUser.getId());
    }

    @Transactional
    public List<ConversationDto> getUserConversations(User currentUser) {
        // Get all users the current user has had conversations with
        List<User> conversationPartners = messageRepository.findConversationPartners(currentUser);
        List<User> conversationSenders = messageRepository.findConversationSenders(currentUser);
        
        // Combine both lists and remove duplicates
        conversationPartners.removeAll(conversationSenders);
        conversationPartners.addAll(conversationSenders);
        
        return conversationPartners.stream()
                .map(partner -> {
                    List<Message> messages = messageRepository.findConversationBetweenUsers(currentUser, partner);
                    Long unreadCount = messageRepository.countUnreadMessages(currentUser);
                    return conversationMapper.toConversationDto(partner, currentUser, messages, unreadCount);
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public List<Message> getConversationByUsername(String username, User currentUser) {
        User otherUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));

        return messageRepository.findConversationBetweenUsers(currentUser, otherUser);
    }
}
