package com.logicq1.crm.modules.organization.dto;

import com.logicq1.crm.modules.organization.entity.Roles;
import com.logicq1.crm.modules.organization.enums.InviteStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ValidateOrganizationInviteResponse {

    private String email;

    private String organizationName;

    private String role;

    private InviteStatus status;

    private LocalDateTime expiresAt;
}