package com.logicq1.crm.modules.audit.events;

import com.logicq1.crm.enums.ActivityVisibility;

import java.util.UUID;

public record EntityUpdatedEvent(
        String entityType,
        UUID entityId,
        UUID organizationId,
        UUID performedBy,
        String fieldName,
        String oldValue,
        String newValue,
        ActivityVisibility visibility
) {
}