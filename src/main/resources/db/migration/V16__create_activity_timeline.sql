CREATE TABLE activity_timeline (
                                   id UUID PRIMARY KEY,

                                   entity_type VARCHAR(50) NOT NULL,
                                   entity_id UUID NOT NULL,

                                   action_type VARCHAR(50) NOT NULL,

                                   field_name VARCHAR(255),

                                   old_value TEXT,
                                   new_value TEXT,

                                   performed_by UUID,

                                   created_at TIMESTAMP NOT NULL
);