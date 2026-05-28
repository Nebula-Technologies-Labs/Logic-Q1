CREATE TABLE entity_tags (
	contact_id UUID NOT NULL,
	tag_id UUID NOT NULL,
	created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
	CONSTRAINT pk_entity_tags PRIMARY KEY (contact_id, tag_id),
	CONSTRAINT fk_entity_tags_contact FOREIGN KEY (contact_id) REFERENCES contact(id) ON DELETE CASCADE,
	CONSTRAINT fk_entity_tags_tag FOREIGN KEY (tag_id) REFERENCES tags(id) ON DELETE CASCADE
);

CREATE INDEX idx_entity_tags_tag_id ON entity_tags (tag_id);

