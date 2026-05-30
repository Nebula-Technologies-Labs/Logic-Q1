package com.logicq1.crm.modules.user.dto;

import com.logicq1.crm.modules.organization.entity.Roles;

public record UserDto(
        String email,
        String name,
        Roles role
) {
}
