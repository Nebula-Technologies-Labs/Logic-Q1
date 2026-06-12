ALTER TABLE activity_timeline
    ADD COLUMN organization_id UUID;

ALTER TABLE activity_timeline
    ADD COLUMN visibility VARCHAR(50);

ALTER TABLE activity_timeline
    ADD COLUMN target_entity_type VARCHAR(50);

ALTER TABLE activity_timeline
    ADD COLUMN target_entity_id UUID;