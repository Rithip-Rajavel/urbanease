package com.urbanease.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.urbanease.config.OneSignalConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class OneSignalService {

    private final OneSignalConfig oneSignalConfig;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public void sendPushNotification(String playerToken, String title, String message, Map<String, Object> data) {
        try {
            Map<String, Object> notification = new HashMap<>();
            notification.put("app_id", oneSignalConfig.getAppId());
            notification.put("include_player_ids", java.util.Arrays.asList(playerToken));
            notification.put("headings", Map.of("en", title));
            notification.put("contents", Map.of("en", message));
            
            if (data != null) {
                notification.put("data", data);
            }

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Basic " + oneSignalConfig.getApiKey());
            headers.set("Content-Type", "application/json");

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(notification, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    oneSignalConfig.getApiUrl(),
                    entity,
                    String.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                log.info("Push notification sent successfully: {}", response.getBody());
            } else {
                log.warn("Push notification failed with status: {}", response.getStatusCode());
            }

        } catch (Exception e) {
            log.error("Error sending push notification: {}", e.getMessage(), e);
        }
    }

    public void sendPushNotificationToUser(Long userId, String title, String message, Map<String, Object> data) {
        // For now, we'll use a simple approach. In a real app, you'd store player tokens per user
        String playerToken = "player_token_for_user_" + userId; // This should come from your database
        sendPushNotification(playerToken, title, message, data);
    }

    public void sendMessageNotification(Long receiverId, String senderName, String messageContent) {
        Map<String, Object> data = new HashMap<>();
        data.put("type", "new_message");
        data.put("sender_id", senderName);
        data.put("message", messageContent);

        String title = "New Message";
        String message = senderName + ": " + messageContent;

        sendPushNotificationToUser(receiverId, title, message, data);
    }
}
