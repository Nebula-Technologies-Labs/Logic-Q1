package com.logicq1.crm.modules.activityTImeline.dto;

import com.logicq1.crm.enums.ActivityVisibility;

public record FieldChange(
        String fieldName,
        String oldValue,
        String newValue,
        ActivityVisibility visibility
) {
}
