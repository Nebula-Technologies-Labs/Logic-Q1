package com.logicq1.crm.modules.contacts.dto;

import com.logicq1.crm.modules.organization.entity.Organization;
import com.logicq1.crm.modules.user.entity.User;

public record ContactDto(
        String email,
        String name,
        String phoneNumber,
        String secondaryEmail,
        String description,
        String note,
        String title,
        String department,
        String ContactSource,
        String country,
        String city,
        String state,
        String contactSource,
        User owner,
        Organization organization
) {
}
