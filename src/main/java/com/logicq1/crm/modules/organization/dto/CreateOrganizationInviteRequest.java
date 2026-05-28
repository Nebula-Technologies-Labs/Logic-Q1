package com.logicq1.crm.modules.organization.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class CreateOrganizationInviteRequest {

    private String email;

    private UUID roleId;
}