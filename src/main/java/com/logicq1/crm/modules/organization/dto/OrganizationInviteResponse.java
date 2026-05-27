package com.logicq1.crm.modules.organization.dto;

import com.logicq1.crm.modules.auth.enums.UserRole;
import com.logicq1.crm.modules.organization.enums.InviteStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrganizationInviteResponse {

    private UUID id;

    private String email;

    private UserRole role;

    private InviteStatus status;

    private UUID organizationId;

    private String token;

    private LocalDateTime expiresAt;
}