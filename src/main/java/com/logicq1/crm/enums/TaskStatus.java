package com.logicq1.crm.enums;

/**
 * Lifecycle of a task. Each value doubles as a column on the kanban board.
 * Mirrors the status picklist of the Zoho CRM Tasks module.
 */
public enum TaskStatus {
    NOT_STARTED,
    IN_PROGRESS,
    WAITING_FOR_INPUT,
    COMPLETED,
    DEFERRED
}
