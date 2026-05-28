CREATE TABLE permissions (
	id UUID PRIMARY KEY,
	name VARCHAR(255) NOT NULL,
	description TEXT,
	role_id UUID NOT NULL,
	CONSTRAINT uk_permissions_role_name UNIQUE (role_id, name),
	CONSTRAINT fk_permissions_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

CREATE INDEX idx_permissions_role_id ON permissions (role_id);
CREATE INDEX idx_permissions_name ON permissions (name);

