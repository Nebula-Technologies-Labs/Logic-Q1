package com.logicq1.crm.modules.contacts.dto;

import java.util.UUID;

public record UpdateContactRequest(

        UUID contactId,

        String name,
        String email,
        String secondaryEmail,
        String phoneNumber,

        String title,
        String department,

        String description,
        String note,

        String country,
        String state,
        String city,

        String contactSource

) {}
