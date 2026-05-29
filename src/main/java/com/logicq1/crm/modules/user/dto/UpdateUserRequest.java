package com.logicq1.crm.modules.user.dto;

public record UpdateUserRequest(
        String firstName,
        String lastName,
        String phoneNumber,
        String jobTitle,
        String department
) {}
