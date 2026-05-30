package com.logicq1.crm.modules.user.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponse(
        UUID id,

        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        String profileImage,

        String jobTitle,
        String department,

        Boolean onBoarded,
        Boolean isActive,
        Boolean emailVerified,

        UUID organizationId,
        UUID roleId,
        UUID createdById,

        LocalDateTime lastLoginAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}