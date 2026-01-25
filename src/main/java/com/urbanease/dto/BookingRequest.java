package com.urbanease.dto;

import com.urbanease.model.Location;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BookingRequest {

    @NotNull(message = "Service ID is required")
    private Long serviceId;

    @NotNull(message = "Provider ID is required")
    private Long providerId;

    @NotNull(message = "Service location is required")
    private Location serviceLocation;

    @Future(message = "Scheduled time must be in the future")
    private LocalDateTime scheduledTime;

    @Positive(message = "Total amount must be positive")
    private BigDecimal totalAmount;

    private String description;
}
