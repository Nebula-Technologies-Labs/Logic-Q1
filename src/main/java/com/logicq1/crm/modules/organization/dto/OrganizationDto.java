package com.logicq1.crm.modules.organization.dto;

import java.util.UUID;

public record OrganizationDto(
        UUID id,
        String name,
        String description,
        String country,
        String state,
        String city,
        String pinCode,
        String category,
        String mobileNumber,
        String websiteUrl,
        String createdAt
) {
}
