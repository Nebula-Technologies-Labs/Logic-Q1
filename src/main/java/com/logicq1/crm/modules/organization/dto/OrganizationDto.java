package com.logicq1.crm.modules.organization.dto;

public record OrganizationDto(
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
