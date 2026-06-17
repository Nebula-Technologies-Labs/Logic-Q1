package com.logicq1.crm.modules.task.dto;

import com.logicq1.crm.enums.TaskPriority;
import com.logicq1.crm.enums.TaskStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Payload for updating a task. Only the non-null fields are applied (partial
 * update), so callers can patch a single attribute without resending the rest.
 */
public record UpdateTaskRequest(
        String subject,
        String description,
        TaskStatus status,
        TaskPriority priority,
        LocalDate dueDate,
        LocalDateTime reminderAt,
        Boolean sendNotificationEmail,
        Boolean recurringActivity,
        UUID ownerId,
        UUID relatedContactId,
        UUID relatedLeadId
) {
}
