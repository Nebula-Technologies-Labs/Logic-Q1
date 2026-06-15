ALTER TABLE activity_timeline
    ADD COLUMN changes jsonb;

ALTER TABLE activity_timeline
DROP COLUMN field_name,
DROP COLUMN old_value,
DROP COLUMN new_value;