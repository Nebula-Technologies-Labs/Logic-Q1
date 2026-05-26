package com.logicq1.crm.modules.organization.dto;

import com.logicq1.crm.modules.auth.enums.UserRole;
import com.logicq1.crm.modules.organization.enums.InviteStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ValidateOrganizationInviteResponse {

    private String email;

    private String organizationName;

    private UserRole role;

    private InviteStatus status;

    private LocalDateTime expiresAt;
}