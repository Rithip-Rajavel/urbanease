package com.urbanease.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // Determine a clean, user-friendly message (never expose raw SQL/JDBC details)
        String friendlyMessage;
        String rawMessage = authException.getMessage();
        if (rawMessage != null && (rawMessage.contains("JDBC") || rawMessage.contains("SQL") || rawMessage.contains("HibernateException"))) {
            friendlyMessage = "Authentication failed due to a server error. Please try again later.";
        } else if (rawMessage != null && rawMessage.toLowerCase().contains("bad credentials")) {
            friendlyMessage = "Invalid username or password.";
        } else if (rawMessage != null && rawMessage.toLowerCase().contains("disabled")) {
            friendlyMessage = "Your account has been disabled. Please contact support.";
        } else if (rawMessage != null && rawMessage.toLowerCase().contains("locked")) {
            friendlyMessage = "Your account has been locked. Please contact support.";
        } else if (rawMessage != null && rawMessage.toLowerCase().contains("expired")) {
            friendlyMessage = "Your session has expired. Please log in again.";
        } else {
            friendlyMessage = "Authentication failed. Please check your credentials and try again.";
        }

        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        body.put("error", "Unauthorized");
        body.put("message", friendlyMessage);
        body.put("path", request.getServletPath());

        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), body);
    }
}
