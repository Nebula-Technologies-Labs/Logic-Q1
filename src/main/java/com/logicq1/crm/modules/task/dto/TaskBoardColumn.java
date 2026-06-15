package com.logicq1.crm.modules.task.dto;

import com.logicq1.crm.enums.TaskStatus;

import java.util.List;

/**
 * One column of the kanban board: a status and the ordered cards within it.
 */
public record TaskBoardColumn(
        TaskStatus status,
        List<TaskDTO> tasks
) {
}
