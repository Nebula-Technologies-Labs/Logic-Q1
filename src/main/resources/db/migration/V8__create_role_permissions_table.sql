CREATE TABLE role_permissions (
	role_id UUID PRIMARY KEY,
	CONSTRAINT fk_role_permissions_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

