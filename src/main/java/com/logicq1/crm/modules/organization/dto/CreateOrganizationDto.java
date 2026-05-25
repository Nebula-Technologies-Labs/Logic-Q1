package com.logicq1.crm.modules.organization.dto;

public record CreateOrganizationDto (
        String name,
        String category,
        String country,
        String websiteUrl
) {
}
