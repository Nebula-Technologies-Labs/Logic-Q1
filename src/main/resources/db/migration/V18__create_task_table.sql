CREATE TABLE task
(
    id                      UUID NOT NULL,
    subject                 VARCHAR(255) NOT NULL,
    description             TEXT,
    status                  VARCHAR(255) NOT NULL,
    priority                VARCHAR(255),
    due_date                DATE,
    reminder_at             TIMESTAMP WITHOUT TIME ZONE,
    send_notification_email BOOLEAN,
    recurring_activity      BOOLEAN,
    board_position          INTEGER NOT NULL,
    owner_id                UUID NOT NULL,
    organization_id         UUID NOT NULL,
    related_contact_id      UUID,
    related_lead_id         UUID,
    created_by              UUID,
    updated_by              UUID,
    created_at              TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at              TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_task PRIMARY KEY (id),
    CONSTRAINT fk_task_owner FOREIGN KEY (owner_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_task_organization FOREIGN KEY (organization_id) REFERENCES organization (id) ON DELETE CASCADE,
    CONSTRAINT fk_task_contact FOREIGN KEY (related_contact_id) REFERENCES contact (id) ON DELETE SET NULL,
    CONSTRAINT fk_task_lead FOREIGN KEY (related_lead_id) REFERENCES lead (id) ON DELETE SET NULL,
    CONSTRAINT fk_task_created_by FOREIGN KEY (created_by) REFERENCES users (id),
    CONSTRAINT fk_task_updated_by FOREIGN KEY (updated_by) REFERENCES users (id)
);

CREATE INDEX idx_task_organization_id ON task (organization_id);
CREATE INDEX idx_task_owner_id ON task (owner_id);
CREATE INDEX idx_task_status ON task (status);
