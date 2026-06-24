package com.logicq1.crm.modules.task.dto;

import com.logicq1.crm.enums.TaskPriority;
import com.logicq1.crm.enums.TaskStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Payload for creating a task. {@code subject} is mandatory; everything else is
 * optional and falls back to sensible defaults in the service layer.
 */
public record CreateTaskRequest(
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
