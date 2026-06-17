CREATE INDEX idx_timeline_entity
    ON activity_timeline(
                         entity_type,
                         entity_id,
                         created_at DESC
        );

CREATE INDEX idx_timeline_org_feed
    ON activity_timeline(
                         organization_id,
                         visibility,
                         created_at DESC
        );