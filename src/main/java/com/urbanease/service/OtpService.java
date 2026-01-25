package com.urbanease.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@Slf4j
public class OtpService {

    private static final int OTP_LENGTH = 6;
    private static final int OTP_EXPIRY_MINUTES = 10;
    private final SecureRandom random = new SecureRandom();

    public String generateOtp() {
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(random.nextInt(10));
        }
        return otp.toString();
    }

    public LocalDateTime getOtpExpiryTime() {
        return LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES);
    }

    public boolean isOtpValid(String otp, LocalDateTime expiryTime) {
        return otp != null && expiryTime != null && 
               LocalDateTime.now().isBefore(expiryTime);
    }

    public boolean isOtpExpired(LocalDateTime expiryTime) {
        return expiryTime == null || LocalDateTime.now().isAfter(expiryTime);
    }
}
