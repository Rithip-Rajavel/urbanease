package com.urbanease.controller;

import com.urbanease.dto.MessageRequest;
import com.urbanease.model.Message;
import com.urbanease.model.User;
import com.urbanease.service.MessageService;
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
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@Tag(name = "Messages", description = "Messaging system APIs")
public class MessageController {

    private final MessageService messageService;

    @PostMapping
    @Operation(summary = "Send a message")
    public ResponseEntity<Message> sendMessage(
            @Valid @RequestBody MessageRequest request,
            @AuthenticationPrincipal User sender) {
        
        Message message = messageService.sendMessage(request, sender);
        return ResponseEntity.ok(message);
    }

    @GetMapping("/conversation/{userId}")
    @Operation(summary = "Get conversation with another user")
    public ResponseEntity<List<Message>> getConversation(
            @PathVariable Long userId,
            @AuthenticationPrincipal User currentUser) {
        
        List<Message> messages = messageService.getConversation(userId, currentUser);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/booking/{bookingId}")
    @Operation(summary = "Get messages for a specific booking")
    public ResponseEntity<List<Message>> getBookingMessages(
            @PathVariable Long bookingId,
            @AuthenticationPrincipal User currentUser) {
        
        List<Message> messages = messageService.getBookingMessages(bookingId, currentUser);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/unread")
    @Operation(summary = "Get unread messages")
    public ResponseEntity<List<Message>> getUnreadMessages(
            @AuthenticationPrincipal User currentUser) {
        
        List<Message> messages = messageService.getUnreadMessages(currentUser);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/unread/count")
    @Operation(summary = "Get unread message count")
    public ResponseEntity<Map<String, Long>> getUnreadMessageCount(
            @AuthenticationPrincipal User currentUser) {
        
        Long count = messageService.getUnreadMessageCount(currentUser);
        Map<String, Long> response = new HashMap<>();
        response.put("unreadCount", count);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/read/{senderId}")
    @Operation(summary = "Mark messages from a sender as read")
    public ResponseEntity<Map<String, String>> markMessagesAsRead(
            @PathVariable Long senderId,
            @AuthenticationPrincipal User currentUser) {
        
        messageService.markMessagesAsRead(senderId, currentUser);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Messages marked as read");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/read/all")
    @Operation(summary = "Mark all messages as read")
    public ResponseEntity<Map<String, String>> markAllMessagesAsRead(
            @AuthenticationPrincipal User currentUser) {
        
        messageService.markAllMessagesAsRead(currentUser);
        Map<String, String> response = new HashMap<>();
        response.put("message", "All messages marked as read");
        return ResponseEntity.ok(response);
    }
}
