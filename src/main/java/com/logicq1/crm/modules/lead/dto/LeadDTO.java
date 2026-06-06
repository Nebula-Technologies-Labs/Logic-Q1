package com.logicq1.crm.modules.lead.dto;

import com.logicq1.crm.enums.Industry;
import com.logicq1.crm.enums.LeadRating;
import com.logicq1.crm.enums.LeadSource;
import com.logicq1.crm.enums.LeadStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record LeadDTO(

        UUID id,

        // Organization
        UUID organizationId,
        String organizationName,

        // Owner
        UUID ownerId,
        String ownerName,

        // Basic Information
        String title,
        String firstName,
        String lastName,
        String company,

        // Contact Information
        String email,
        String secondaryEmail,
        String phone,
        String mobile,
        String fax,
        String website,
        String twitterHandle,

        // Business Information
        BigDecimal annualRevenue,
        Integer employeeCount,
        Boolean emailOptOut,

        // Address
        String street,
        String city,
        String state,
        String postalCode,
        String country,

        // Description
        String description,

        // Lookup Fields
        Industry industry,
        LeadStatus leadStatus,
        LeadSource leadSource,
        LeadRating rating,

        // Conversion Information
        Boolean converted,
        LocalDateTime convertedAt,

        UUID convertedById,
        String convertedByName,

        UUID convertedContactId,

        // Audit
        UUID createdById,
        String createdByName,

        UUID updatedById,
        String updatedByName,

        LocalDateTime createdAt,
        LocalDateTime updatedAt

) {}