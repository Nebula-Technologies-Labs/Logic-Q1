CREATE TABLE tags (
	id UUID PRIMARY KEY,
	organization_id UUID NOT NULL,
	name VARCHAR(255) NOT NULL,
	color VARCHAR(32),
	CONSTRAINT uk_tags_organization_name UNIQUE (organization_id, name),
	CONSTRAINT fk_tags_organization FOREIGN KEY (organization_id) REFERENCES organization(id) ON DELETE CASCADE
);

CREATE INDEX idx_tags_organization_id ON tags (organization_id);

