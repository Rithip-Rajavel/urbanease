package com.urbanease.dto;

import com.urbanease.model.UserRole;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RoleChangeRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "New role is required")
    private UserRole newRole;
}
