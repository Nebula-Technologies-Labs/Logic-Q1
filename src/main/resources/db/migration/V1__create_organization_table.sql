CREATE TABLE organization (
	id UUID PRIMARY KEY,
	name VARCHAR(255) NOT NULL,
	description TEXT,
	admin_id UUID UNIQUE,
	mobile_number VARCHAR(50),
	website_url VARCHAR(255),
	category VARCHAR(255),
	country VARCHAR(255),
	state VARCHAR(255),
	city VARCHAR(255),
	pin_code VARCHAR(20),
	created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
	updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_organization_name ON organization (name);

