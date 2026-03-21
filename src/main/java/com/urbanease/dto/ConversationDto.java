package com.urbanease.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConversationDto {
    private Long userId;
    private String username;
    private String userProfileImage;
    private LocalDateTime lastMessageTime;
    private String lastMessageContent;
    private Long unreadCount;
    private boolean isOnline;
    private List<MessageDto> messages;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MessageDto {
        private Long id;
        private String content;
        private LocalDateTime createdAt;
        private boolean isRead;
        private boolean isFromCurrentUser;
        private String senderName;
    }
}
