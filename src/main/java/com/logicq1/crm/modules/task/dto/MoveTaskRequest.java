package com.logicq1.crm.modules.task.dto;

import com.logicq1.crm.enums.TaskStatus;

/**
 * Payload for a kanban drag-and-drop: move a task to {@code status} column and
 * drop it at {@code position} (zero-based index) within that column.
 */
public record MoveTaskRequest(
        TaskStatus status,
        Integer position
) {
}
