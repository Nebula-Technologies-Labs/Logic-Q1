package com.logicq1.crm.modules.lead.dto;

import com.logicq1.crm.enums.Industry;
import com.logicq1.crm.enums.LeadRating;
import com.logicq1.crm.enums.LeadSource;
import com.logicq1.crm.enums.LeadStatus;

import java.math.BigDecimal;
import java.util.UUID;

public record UpdateLeadRequest(

        UUID leadId,

        UUID ownerId,

        String title,
        String firstName,
        String lastName,
        String company,

        String email,
        String secondaryEmail,
        String phone,
        String mobile,
        String fax,
        String website,
        String twitterHandle,

        BigDecimal annualRevenue,
        Integer employeeCount,
        Boolean emailOptOut,

        String street,
        String city,
        String state,
        String postalCode,
        String country,

        String description,

        Industry industry,
        LeadStatus leadStatus,
        LeadSource leadSource,
        LeadRating rating

) {
}