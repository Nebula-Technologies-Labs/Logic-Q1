package com.logicq1.crm.modules.contacts.dto;

public record CreateContactDto(
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
        String contactSource

) {
}
