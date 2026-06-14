package com.logicq1.crm.modules.audit.events;

import com.logicq1.crm.enums.ActivityVisibility;

import java.util.UUID;

public record EntityDeletedEvent(
        String entityType,
        UUID entityId,
        UUID organizationId,
        UUID performedBy,
        ActivityVisibility visibility
) {
}