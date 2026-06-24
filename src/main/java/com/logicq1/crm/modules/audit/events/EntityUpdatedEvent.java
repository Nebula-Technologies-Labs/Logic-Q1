package com.logicq1.crm.modules.audit.events;

import com.logicq1.crm.enums.ActivityVisibility;
import com.logicq1.crm.modules.activityTImeline.dto.FieldChange;

import java.util.List;
import java.util.UUID;

public record EntityUpdatedEvent(
        String entityType,
        UUID entityId,
        UUID organizationId,
        UUID performedBy,
        List<FieldChange> changes
) {
}