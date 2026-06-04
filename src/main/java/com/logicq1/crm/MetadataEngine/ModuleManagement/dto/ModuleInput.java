package com.logicq1.crm.MetadataEngine.ModuleManagement.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModuleInput {
    private UUID organizationId;
    private String name;
    private String apiName;
    private String description;
    private String icon;
    private Boolean isBuiltIn;
    private Boolean isActive;
    private UUID createdBy;
}