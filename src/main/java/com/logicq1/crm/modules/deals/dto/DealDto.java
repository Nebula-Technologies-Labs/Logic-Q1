package com.logicq1.crm.modules.deals.dto;

import com.logicq1.crm.enums.DealStage;
import com.logicq1.crm.enums.DealType;
import com.logicq1.crm.enums.LeadSource;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Builder
public class DealDto {

    private UUID id;

    private String dealName;

    private BigDecimal amount;

    private BigDecimal expectedRevenue;

    private DealStage stage;

    private DealType type;

    private Integer probability;

    private String nextStep;

    private LeadSource leadSource;

    private String campaignSource;

    private LocalDate closingDate;

    private String description;

    private UUID contactId;

    private UUID ownerId;

    private String ownerName;

    private UUID organizationId;

    private UUID createdBy;

    private Instant createdAt;

    private Instant updatedAt;
}
