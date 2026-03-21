package com.urbanease.service;

import com.urbanease.dto.ConversationDto;
import com.urbanease.model.Message;
import com.urbanease.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConversationMapper {

    public ConversationDto toConversationDto(User otherUser, User currentUser, List<Message> messages, Long unreadCount) {
        ConversationDto dto = new ConversationDto();
        dto.setUserId(otherUser.getId());
        dto.setUsername(otherUser.getUsername());
        dto.setUserProfileImage(getProfileImageUrl(otherUser));
        dto.setUnreadCount(unreadCount);
        dto.setOnline(false); // TODO: Implement online status tracking
        
        if (!messages.isEmpty()) {
            Message lastMessage = messages.get(0); // Assuming messages are ordered by date desc
            dto.setLastMessageTime(lastMessage.getCreatedAt());
            dto.setLastMessageContent(lastMessage.getContent());
        }
        
        List<ConversationDto.MessageDto> messageDtos = messages.stream()
                .map(msg -> toMessageDto(msg, currentUser))
                .collect(Collectors.toList());
        dto.setMessages(messageDtos);
        
        return dto;
    }

    private ConversationDto.MessageDto toMessageDto(Message message, User currentUser) {
        ConversationDto.MessageDto dto = new ConversationDto.MessageDto();
        dto.setId(message.getId());
        dto.setContent(message.getContent());
        dto.setCreatedAt(message.getCreatedAt());
        dto.setRead(message.isRead());
        dto.setFromCurrentUser(message.getSender().getId().equals(currentUser.getId()));
        dto.setSenderName(message.getSender().getUsername());
        return dto;
    }

    private String getProfileImageUrl(User user) {
        // TODO: Get profile image from ProviderProfile if user is a provider
        return null;
    }
}
