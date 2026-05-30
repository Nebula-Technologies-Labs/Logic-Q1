package com.logicq1.crm.modules.organization.dto;

import com.logicq1.crm.modules.organization.entity.Roles;
import com.logicq1.crm.modules.organization.enums.InviteStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrganizationInviteResponse {

    private String token;
}