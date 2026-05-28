package com.logicq1.crm.modules.organization.dto;

import com.logicq1.crm.modules.organization.enums.UserRole;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateOrganizationInviteRequest {

    private String email;

    private UserRole role;
}