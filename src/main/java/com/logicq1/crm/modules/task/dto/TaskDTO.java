package com.logicq1.crm.modules.task.dto;

import com.logicq1.crm.enums.TaskPriority;
import com.logicq1.crm.enums.TaskStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Read model for a task, with related entities flattened to ids + display names.
 */
public record TaskDTO(
        UUID id,
        String subject,
        String description,
        TaskStatus status,
        TaskPriority priority,
        LocalDate dueDate,
        LocalDateTime reminderAt,
        Boolean sendNotificationEmail,
        Boolean recurringActivity,
        Integer boardPosition,

        UUID ownerId,
        String ownerName,

        UUID organizationId,

        UUID relatedContactId,
        String relatedContactName,

        UUID relatedLeadId,
        String relatedLeadName,

        UUID createdById,
        UUID updatedById,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
