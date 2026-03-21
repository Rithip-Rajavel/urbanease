package com.urbanease.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OneSignalConfig {

    @Value("${onesignal.app.id}")
    private String appId;

    @Value("${onesignal.api.key}")
    private String apiKey;

    @Value("${onesignal.api.url:https://onesignal.com/api/v1/notifications}")
    private String apiUrl;

    public String getAppId() {
        return appId;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getApiUrl() {
        return apiUrl;
    }
}
